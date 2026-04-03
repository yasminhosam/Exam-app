package com.example.onlineexaminationsystem.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.onlineexaminationsystem.domain.model.AnswerSnapshot
import com.example.onlineexaminationsystem.domain.model.SubmittedExam
import com.example.onlineexaminationsystem.domain.model.SubmittedExamWithSnapshots
import com.example.onlineexaminationsystem.domain.model.TopStudent
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    //save the result of the taken exam
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmittedExam(submittedExam: SubmittedExam)

    //save the answers
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshots(snapshot:List<AnswerSnapshot>)

    @Query("SELECT * FROM answer_snapshots WHERE submitted_exam_id= :submittedExamId")
     fun getSnapshots(submittedExamId:String):Flow<List<AnswerSnapshot>>


    @Query("SELECT * FROM submitted_exams WHERE student_id= :studentId")
     fun getStudentHistory(studentId:String):Flow<List<SubmittedExam>>
//
//    @Query("""
//        SELECT studentName AS name, MAX(score) AS score, grade
//        FROM submitted_exams
//        GROUP BY student_id
//        ORDER BY score DESC
//        LIMIT :limit
//    """)
//    fun getTopStudents(limit: Int = 10): Flow<List<TopStudent>>

    @Transaction
    @Query("SELECT * FROM submitted_exams WHERE isSynced = 0")
    suspend fun getPendingSubmissionsWithSnapshots(): List<SubmittedExamWithSnapshots>

    @Query("UPDATE submitted_exams SET isSynced = :isSynced WHERE id = :submissionId")
    suspend fun updateSubmissionSyncStatus(submissionId: String, isSynced: Boolean)


    @Query("UPDATE answer_snapshots SET isSynced = 1 WHERE submitted_exam_id = :submittedExamId")
    suspend fun updateSnapshotsSyncStatus(submittedExamId: String)
}