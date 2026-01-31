package com.example.onlineexaminationsystem.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.onlineexaminationsystem.data.model.Category
import com.example.onlineexaminationsystem.data.model.Exam
import com.example.onlineexaminationsystem.data.model.ExamWithDetails
import com.example.onlineexaminationsystem.data.model.Question

@Dao
interface ExamDao {
    @Insert
    suspend fun insertExam(exam: Exam):Long

    @Query("DELETE FROM exams WHERE id = :examId")
    suspend fun deleteExam(examId:Long)

    @Transaction
    @Query("SELECT * FROM exams WHERE category_id= :categoryId")
    suspend fun getExamByCategoryId(categoryId: Long):List<ExamWithDetails>

    @Query("SELECT * FROM exams")
    suspend fun getAllExams():List<ExamWithDetails>

    @Query("SELECT name FROM categories WHERE id= :categoryId")
    suspend fun getCategoryNameById(categoryId: Long):String


    @Transaction
    @Query("SELECT title FROM exams WHERE id= :examId")
    suspend fun getExamNameById(examId:Long):String

    @Transaction
    @Query("SELECT * FROM exams WHERE id= :examId")
    suspend fun getExamById(examId:Long):ExamWithDetails

    @Insert
    suspend fun insertQuestions(questions: List<Question>)

    @Insert
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Query("SELECT * FROM categories ")
    suspend fun getAllCategories():List<Category>
}