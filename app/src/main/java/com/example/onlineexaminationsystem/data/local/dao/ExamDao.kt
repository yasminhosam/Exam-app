package com.example.onlineexaminationsystem.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.onlineexaminationsystem.domain.model.Category
import com.example.onlineexaminationsystem.domain.model.Exam
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.domain.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExam(examId: String)

    @Transaction
    @Query("SELECT * FROM exams WHERE category_id = :categoryId")
    fun getExamByCategoryId(categoryId: String): Flow<List<ExamWithDetails>>

    @Transaction
    @Query("SELECT * FROM exams WHERE isDeleted = 0")
    fun getAllUnDeletedExams(): Flow<List<ExamWithDetails>>


    @Transaction
    @Query("SELECT * FROM exams WHERE teacher_id = :teacherId AND isDeleted = 0 ORDER BY rowid DESC")
    fun getExamsByTeacher(teacherId: String): Flow<List<ExamWithDetails>>

    @Query("SELECT name FROM categories WHERE id = :categoryId")
    suspend fun getCategoryNameById(categoryId: String): String

    @Query("SELECT title FROM exams WHERE id = :examId")
    suspend fun getExamNameById(examId: String): String

    @Transaction
    @Query("SELECT * FROM exams WHERE id = :examId")
    suspend fun getExamById(examId: String): ExamWithDetails

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM exams WHERE isSynced = 0")
    suspend fun getUnsyncedExamsWithQuestions(): List<ExamWithDetails>

    @Transaction
    @Query("SELECT *FROM exams WHERE isDeleted = 1")
    suspend fun getUnsyncedDeletedExam():List<ExamWithDetails>

     @Query("UPDATE exams SET isDeleted = 1 WHERE id = :examId")
     suspend fun markExamAsDeleted(examId: String)

    @Query("UPDATE exams SET isSynced = :isSynced WHERE id = :examId")
    suspend fun updateExamSyncStatus(examId: String, isSynced: Boolean)

}