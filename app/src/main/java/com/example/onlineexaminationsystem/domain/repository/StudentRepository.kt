package com.example.onlineexaminationsystem.domain.repository

import com.example.onlineexaminationsystem.domain.model.AnswerSnapshot
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.domain.model.SubmittedExam
import kotlinx.coroutines.flow.Flow

interface StudentRepository {

    suspend fun submitExam(studentId: String, studentName: String, examWithDetails: ExamWithDetails, studentAnswers: Map<String, Int>)


     fun getExamHistory(studentId: String): Flow<List<SubmittedExam>>

     fun getAnswerSnapshots(submittedExamId: String): Flow<List<AnswerSnapshot>>
    suspend fun fetchStudentHistoryFromCloud(studentId: String)

}