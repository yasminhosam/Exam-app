package com.example.onlineexaminationsystem.data.repository

import com.example.onlineexaminationsystem.data.model.Category
import com.example.onlineexaminationsystem.data.model.Exam
import com.example.onlineexaminationsystem.data.model.Question

import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.data.model.ExamWithDetails
import javax.inject.Inject
import kotlin.time.Duration

class ExamRepository @Inject constructor(
    private val examDao: ExamDao
) {

    suspend fun addExam(
        title: String,
        category: Category,
        questions: MutableList<Question>,
        duration: Duration,
        passPercentage: Int
    ) {

        var totalScore: Int =0
        questions.forEach{q->
           totalScore+= q.mark
        }
        val exam = Exam(
            id = 0,
            title = title,
            categoryId = category.id,
            duration = duration,
            passPercentage = passPercentage,
            totalScore = totalScore
        )
        val newExamId = examDao.insertExam(exam)
        val questionsToExam=questions.map { it.copy(examId = newExamId) }
        examDao.insertQuestions(questionsToExam)
    }


    suspend fun deleteExam(id: Long) {
        examDao.deleteExam(id)
    }

    suspend fun getExamsByCategory(categoryId: Long): List<ExamWithDetails> {
        return examDao.getExamByCategoryId(categoryId)
    }
    suspend fun getExamById(id: Long): ExamWithDetails {
        return examDao.getExamById(id)
    }

    suspend fun addQuestionToExam(
        examId: Long,
        text: String,
        options: List<String>,
        correctAnswerIndex: Int,
        mark: Int
    ) {

        val newQuestion = Question(
            id = 0,
            examId = examId,
            text = text,
            options = options,
            correctAnswer = correctAnswerIndex,
            mark = mark
        )

        examDao.insertQuestion(newQuestion)
    }

    suspend fun getAllCategories():List<Category>{
      return  examDao.getAllCategories()
    }

    suspend fun getAllExams(): List<ExamWithDetails> {
        return examDao.getAllExams()
    }


    suspend fun getCategoryName(categoryId: Long):String{
        return examDao.getCategoryNameById(categoryId)
    }

}