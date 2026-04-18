package com.example.onlineexaminationsystem.data.repository

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.onlineexaminationsystem.domain.model.Status

import com.example.onlineexaminationsystem.data.local.dao.StudentDao
import com.example.onlineexaminationsystem.data.remote.SubmittedExamDto
import com.example.onlineexaminationsystem.domain.GradeCalculator
import com.example.onlineexaminationsystem.data.sync.SyncWorker
import com.example.onlineexaminationsystem.domain.model.AnswerSnapshot
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.domain.model.SubmittedExam
import com.example.onlineexaminationsystem.domain.repository.StudentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class StudentRepositoryImpl @Inject constructor(
    private val studentDao: StudentDao,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager
): StudentRepository {
    //studentAnswers key=questionId,value = selected option
    override suspend fun submitExam(
        studentId: String,
        studentName: String,
        examWithDetails: ExamWithDetails,
        studentAnswers: Map<String, Int>
    ) {
        var score = 0
        var totalMark = 0
        val snapshots = mutableListOf<AnswerSnapshot>()
        val submittedExamId = UUID.randomUUID().toString()

        examWithDetails.questions.forEach { question ->
            totalMark += question.mark
            val selectedOptionIndex = studentAnswers[question.id] ?: -1
            if (selectedOptionIndex == question.correctAnswer) {
                score += question.mark
            }
            snapshots.add(
                AnswerSnapshot(
                    id = UUID.randomUUID().toString(),
                    submittedExamId = submittedExamId,
                    questionText = question.text,
                    options = question.options,
                    examMark = question.mark,
                    studentAnswerIndex = selectedOptionIndex,
                    correctAnswerIndex = question.correctAnswer,
                    isSynced = false
                )
            )
        }

        val studentPercentage = ((score.toDouble() / totalMark) * 100).toInt()
        val status =
            if (studentPercentage >= examWithDetails.exam.passPercentage) Status.PASSED else Status.FAILED

        val gradeLetter =
            GradeCalculator.calculateGradeLetter(studentPercentage, examWithDetails.exam.passPercentage)

        val submittedExam = SubmittedExam(
            id = submittedExamId,
            examId = examWithDetails.exam.id,
            studentId = studentId,
            studentName = studentName,
            score = score,
            grade = gradeLetter,
            status = status,
            date = Date().time,
            isSynced = false
        )

        studentDao.insertSubmittedExam(submittedExam)
        studentDao.insertSnapshots(snapshots)
        triggerSync()


    }
    private fun triggerSync(){
        Log.d("SyncWorker", "start sync request")
        val request= OneTimeWorkRequestBuilder<SyncWorker>()
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

        Log.d("SyncWorker", "end sync request")
    }

    override  fun getExamHistory(studentId: String) = studentDao.getStudentHistory(studentId)
    override  fun getAnswerSnapshots(submittedExamId: String) =
        studentDao.getSnapshots(submittedExamId)

    override suspend fun fetchStudentHistoryFromCloud(studentId: String) {
        try {
            val snapshot = firestore.collection("submitted_exams")
                .whereEqualTo("studentId", studentId)
                .get()
                .await()

            val submissionsToInsert = mutableListOf<SubmittedExam>()
            val snapshotsToInsert = mutableListOf<AnswerSnapshot>()

            for (document in snapshot.documents) {
                val dto = document.toObject(SubmittedExamDto::class.java) ?: continue

                val submission = SubmittedExam(
                    id = dto.id,
                    examId = dto.examId,
                    studentId = dto.studentId,
                    studentName = dto.studentName,
                    score = dto.score,
                    grade = dto.grade,
                    status = Status.valueOf(dto.status),
                    date = dto.date,
                    isSynced = true
                )
                submissionsToInsert.add(submission)

                dto.snapshots.forEach { snapDto ->
                    snapshotsToInsert.add(
                        AnswerSnapshot(
                            id = UUID.randomUUID().toString(),
                            submittedExamId = dto.id,
                            questionText = snapDto.questionText,
                            options = snapDto.options,
                            examMark = snapDto.examMark,
                            studentAnswerIndex = snapDto.studentAnswerIndex,
                            correctAnswerIndex = snapDto.correctAnswerIndex,
                            isSynced = true
                        )
                    )
                }
            }

            submissionsToInsert.forEach { studentDao.insertSubmittedExam(it) }
            studentDao.insertSnapshots(snapshotsToInsert)

            Log.d("DownwardSync", "Successfully fetched ${submissionsToInsert.size} submissions.")

        } catch (e: Exception) {
            Log.e("DownwardSync", "Failed to fetch student history", e)
        }    }


}



