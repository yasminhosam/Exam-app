package com.example.onlineexaminationsystem.data.repository

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.domain.model.Category
import com.example.onlineexaminationsystem.domain.model.Exam
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.domain.model.Question
import com.example.onlineexaminationsystem.data.remote.ExamDto
import com.example.onlineexaminationsystem.data.sync.SyncWorker
import com.example.onlineexaminationsystem.domain.repository.ExamRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


class ExamRepositoryImpl @Inject constructor(
    private val examDao: ExamDao,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
) : ExamRepository {

    override fun getAllExams(): Flow<List<ExamWithDetails>> = examDao.getAllUnDeletedExams()

    override suspend fun getExamById(id: String): ExamWithDetails {
        return examDao.getExamById(id)
            ?: throw Exception("Exam with ID $id not found in local database")
    }

    override fun getExamsByCategory(categoryId: String): Flow<List<ExamWithDetails>> =
        examDao.getExamByCategoryId(categoryId)

    override fun getAllCategories(): Flow<List<Category>> = examDao.getAllCategories()

    override suspend fun getCategoryName(categoryId: String): String =
        examDao.getCategoryNameById(categoryId)

    override fun getExamsByTeacher(teacherId: String): Flow<List<ExamWithDetails>> =
        examDao.getExamsByTeacher(teacherId)

    override suspend fun addExam(
        teacherId: String,
        title: String,
        category: Category,
        questions: MutableList<Question>,
        duration: Duration,
        passPercentage: Int
    ) {
        val examId = UUID.randomUUID().toString()
        val totalScore = questions.sumOf { it.mark }

        val newExam = Exam(
            id = examId,
            teacherId = teacherId,
            categoryId = category.id,
            title = title,
            duration = duration,
            passPercentage = passPercentage,
            totalScore = totalScore,
            isSynced = false,
            isDeleted = false
        )
        val newQuestions = questions.map {
            it.copy(examId = examId, id = UUID.randomUUID().toString())
        }
        examDao.insertExam(newExam)
        examDao.insertQuestions(newQuestions)
        triggerSync()
    }

    override suspend fun addQuestionToExam(
        examId: String, text: String, options: List<String>, correctAnswerIndex: Int, mark: Int
    ) {
        val newQuestion = Question(
            id = UUID.randomUUID().toString(),
            examId = examId,
            text = text,
            options = options,
            correctAnswer = correctAnswerIndex,
            mark = mark,
            isSynced = false
        )
        examDao.insertQuestion(newQuestion)
        triggerSync()
    }

    override suspend fun deleteExam(id: String) {
        examDao.markExamAsDeleted(id)
        triggerSync()
    }

    override suspend fun updateExamWithQuestions(
        examId: String,
        title: String,
        categoryId: String,
        questions: MutableList<Question>,
        duration: Duration,
        passPercentage: Int
    ) {
        val totalScore = questions.sumOf { it.mark }
        val examWithDetails = examDao.getExamById(examId)

        val updatedExam = examWithDetails?.exam?.copy(
            title = title,
            categoryId = categoryId,
            duration = duration,
            passPercentage = passPercentage,
            totalScore = totalScore,
            isSynced = false
        )

        val keptQuestionIds = questions.map { it.id }
        examDao.deleteRemovedQuestions(examId, keptQuestionIds)

        val updatedQuestions = questions.map {
            it.copy(examId = examId, isSynced = false)
        }

        if (updatedExam != null) {
            examDao.updateExam(updatedExam)
        }
        examDao.insertQuestions(updatedQuestions)
        triggerSync()
    }

    override suspend fun fetchTeacherExamsFromCloud(teacherId: String) {
        try {
            val snapshot = firestore.collection("exams")
                .whereEqualTo("teacherId", teacherId)
                .get()
                .await()

            val examsToInsert = mutableListOf<Exam>()
            val questionsToInsert = mutableListOf<Question>()

            for (document in snapshot.documents) {
                val dto = document.toObject(ExamDto::class.java) ?: continue


                val exam = Exam(
                    id = dto.id,
                    teacherId = dto.teacherId,
                    categoryId = dto.categoryId,
                    title = dto.title,
                    duration = dto.durationMillis.milliseconds,
                    passPercentage = dto.passPercentage,
                    totalScore = dto.totalScore,
                    isSynced = true,
                    isDeleted = false
                )
                examsToInsert.add(exam)

                // BUG FIX : Questions were assigned random new IDs on every sync.
                // When the teacher logs back in, each sync run inserts a fresh duplicate set
                // of questions alongside the originals (because the PK never matches an
                // existing row, so REPLACE never fires). Use qDto.id so the same Firestore
                // question always maps to the same Room row and upserts cleanly.
                dto.questions.forEach { qDto ->
                    val questionId=if (qDto.id.isNotBlank()){
                        qDto.id
                    }else{
                        UUID.nameUUIDFromBytes(
                            "${exam.id}|${qDto.text}".toByteArray()
                        ).toString()
                    }

                    questionsToInsert.add(
                        Question(
                            id = questionId,
                            examId = dto.id,
                            text = qDto.text,
                            options = qDto.options,
                            correctAnswer = qDto.correctAnswer,
                            mark = qDto.mark,
                            isSynced = true
                        )
                    )
                }
            }


            examDao.insertExams(examsToInsert)
            examDao.insertQuestions(questionsToInsert)

            Log.d("DownwardSync", "Synced ${examsToInsert.size} teacher exam(s) from Firestore.")
        } catch (e: Exception) {
            Log.e("DownwardSync", "Failed to fetch teacher exams", e)
        }
    }

    override suspend fun fetchAllAvailableExamsFromCloud() {
        try {
            val snapshot = firestore.collection("exams").get().await()

            val examsToInsert = mutableListOf<Exam>()
            val questionsToInsert = mutableListOf<Question>()

            for (document in snapshot.documents) {
                val dto = document.toObject(ExamDto::class.java) ?: continue

                val exam = Exam(
                    id = dto.id,
                    teacherId = dto.teacherId,
                    categoryId = dto.categoryId,
                    title = dto.title,
                    duration = dto.durationMillis.milliseconds,
                    passPercentage = dto.passPercentage,
                    totalScore = dto.totalScore,
                    isSynced = true,
                    isDeleted = false
                )
                examsToInsert.add(exam)


                dto.questions.forEach { qDto ->
                    val questionId=if (qDto.id.isNotBlank()){
                        qDto.id
                    }else{
                        UUID.nameUUIDFromBytes(
                            "${exam.id}|${qDto.text}".toByteArray()
                        ).toString()
                    }
                    questionsToInsert.add(
                        Question(
                            id = questionId,
                            examId = dto.id,
                            text = qDto.text,
                            options = qDto.options,
                            correctAnswer = qDto.correctAnswer,
                            mark = qDto.mark,
                            isSynced = true
                        )
                    )
                }
            }

            examDao.insertExams(examsToInsert)
            examDao.insertQuestions(questionsToInsert)

            Log.d("DownwardSync", "Synced ${examsToInsert.size} available exam(s) for student.")
        } catch (e: Exception) {
            Log.e("DownwardSync", "Failed to fetch available exams", e)
        }
    }

    private fun triggerSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "sync_work",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}