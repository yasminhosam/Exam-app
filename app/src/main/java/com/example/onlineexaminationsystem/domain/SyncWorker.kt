package com.example.onlineexaminationsystem.domain

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.data.local.dao.StudentDao
import com.example.onlineexaminationsystem.data.mapper.toDto
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val examDao: ExamDao,
    private val studentDao: StudentDao,
    private val fireStore: FirebaseFirestore


    ): CoroutineWorker(context,workerParams) {
    override suspend fun doWork(): Result {
        return try {
        Log.d("SyncWorker", "Worker started!")
            syncExams()
            syncSubmissions()
            syncDeletedExam()
            Log.d("SyncWorker", "Worker finished!")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Worker failed", e)
            Result.retry()
        }
    }
    private suspend fun syncExams(){
        val unsyncedExams = examDao.getUnsyncedExamsWithQuestions()
        Log.d("SyncWorker", "unsyncedExams: $unsyncedExams")
        for (examWithDetails in unsyncedExams) {
            val dto = examWithDetails.exam.toDto(examWithDetails.questions)

            fireStore.collection("exams")
                .document(dto.id)
                .set(dto)
                .await()

            examDao.updateExamSyncStatus(examWithDetails.exam.id, true)
        }
    }

    private suspend fun syncSubmissions(){
        val pending = studentDao.getPendingSubmissionsWithSnapshots()
        Log.d("SyncWorker", "pending: $pending")
        for (item in pending) {

            val dto = item.submission.toDto(item.snapshots)

            fireStore.collection("submitted_exams")
                .document(dto.id)
                .set(dto)
                .await()

            studentDao.updateSubmissionSyncStatus(item.submission.id, true)
            studentDao.updateSnapshotsSyncStatus(item.submission.id)
        }
    }
    private suspend fun syncDeletedExam(){
        val unsyncedExams = examDao.getUnsyncedDeletedExam()
        for (examWithDetails in unsyncedExams) {
            val dto = examWithDetails.exam.toDto(examWithDetails.questions)

            fireStore.collection("exams")
                .document(dto.id).delete().await()

            examDao.deleteExam(examWithDetails.exam.id)


        }
    }

}