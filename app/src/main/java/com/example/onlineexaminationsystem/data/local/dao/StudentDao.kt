package com.example.onlineexaminationsystem.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.onlineexaminationsystem.data.model.AnswerSnapshot
import com.example.onlineexaminationsystem.data.model.SubmittedExam
import com.example.onlineexaminationsystem.data.model.TopStudent
import com.example.onlineexaminationsystem.data.model.User

@Dao
interface StudentDao {

    //save the result of the taken exam
    @Insert
    suspend fun insertSubmittedExam(submittedExam: SubmittedExam):Long

    //save the answers
    @Insert
    suspend fun insertSnapshots(snapshot:List<AnswerSnapshot>)

    @Query("SELECT * FROM answer_snapshots WHERE submitted_exam_id= :submittedExamId")
    suspend fun getSnapshots(submittedExamId:Long):List<AnswerSnapshot>


    @Query("SELECT * FROM submitted_exams WHERE student_id= :studentId")
    suspend fun getStudentHistory(studentId:Long):List<SubmittedExam>

    @Query("SELECT users.name,submitted_exams.score,submitted_exams.grade " +
            " FROM submitted_exams " +
            "INNER JOIN users " +
            "ON submitted_exams.student_id=users.id " +
            "WHERE exam_id= :examId " +
            "AND status='PASSED' " +
            "ORDER by score desc ")
    suspend fun getTopStudents(examId:Long):List<TopStudent>

    @Query("SELECT * FROM users WHERE id=:id ")
    suspend fun getStudentById(id:Long): User


}