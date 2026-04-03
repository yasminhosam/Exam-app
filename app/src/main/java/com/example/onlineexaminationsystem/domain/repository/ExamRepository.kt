package com.example.onlineexaminationsystem.domain.repository

import com.example.onlineexaminationsystem.domain.model.Category
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.domain.model.Question
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

interface ExamRepository {

    fun getExamsByCategory(categoryId: String): Flow<List<ExamWithDetails>>
    suspend fun getExamById(id: String): ExamWithDetails
    fun getAllCategories(): Flow<List<Category>>
    fun getAllExams(): Flow<List<ExamWithDetails>>
    suspend fun getCategoryName(categoryId: String): String


    fun getExamsByTeacher(teacherId: String): Flow<List<ExamWithDetails>>

    suspend fun addExam(
        teacherId: String,
        title: String,
        category: Category,
        questions: MutableList<Question>,
        duration: Duration,
        passPercentage: Int
    )

    suspend fun addQuestionToExam(examId: String, text: String, options: List<String>, correctAnswerIndex: Int, mark: Int)
    suspend fun deleteExam(id: String)
    suspend fun fetchTeacherExamsFromCloud(teacherId: String)
    suspend fun fetchAllAvailableExamsFromCloud()
}