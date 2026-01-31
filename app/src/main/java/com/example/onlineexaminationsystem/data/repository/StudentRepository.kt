package com.example.onlineexaminationsystem.data.repository

import com.example.onlineexaminationsystem.data.model.Status

import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.data.local.dao.StudentDao
import com.example.onlineexaminationsystem.data.model.AnswerSnapshot
import com.example.onlineexaminationsystem.data.model.ExamWithDetails
import com.example.onlineexaminationsystem.data.model.SubmittedExam
import java.util.Date
import javax.inject.Inject


class StudentRepository @Inject constructor(

    private val studentDao: StudentDao
) {
    //studentAnswers key=questionId,value = selected option


    suspend fun submitExam(
        studentId: Long,
        examWithDetails: ExamWithDetails,
        studentAnswers: Map<Long, Int>
    ) {
        var score = 0
        var totalMark=0
        val snapshots= mutableListOf<AnswerSnapshot>()

        examWithDetails.questions.forEach { question ->
            totalMark+=question.mark
            val selectedOptionIndex = studentAnswers[question.id]?:-1
            if ( selectedOptionIndex == question.correctAnswer) {
                score += question.mark
            }
            snapshots.add(
                AnswerSnapshot(
                submittedExamId = 0,
                questionText = question.text,
                options = question.options,
                examMark = question.mark,
                studentAnswerIndex = selectedOptionIndex,
                correctAnswerIndex = question.correctAnswer
            )
            )

        }


        val studentPercentage = ((score.toDouble() /totalMark) * 100).toInt()
        val status = if (studentPercentage >= examWithDetails.exam.passPercentage) Status.PASSED else Status.FAILED

        val submittedExam = SubmittedExam(
            id = 0,
            examId = examWithDetails.exam.id,
            studentId = studentId,
            score = score,
            grade = calculateGradeLetter(studentPercentage,examWithDetails.exam.passPercentage),
            status = status,
            date = Date().time
        )

        val newResultId=studentDao.insertSubmittedExam(submittedExam)
        val linkedSnapshots=snapshots.map { it.copy(submittedExamId = newResultId) }
        studentDao.insertSnapshots(linkedSnapshots)




    }




    private fun calculateGradeLetter(studentScore: Int, passPercentage: Int): Char {
        if (studentScore < passPercentage)
            return 'F'

        val gap = studentScore - passPercentage

        return when {
            (gap >= 30) -> 'A'
            (gap >= 20) -> 'B'
            (gap >= 10) -> 'C'
            else -> 'D'
        }

    }

    suspend fun getExamHistory(studentId: Long): List<SubmittedExam> {
       return studentDao.getStudentHistory(studentId)
    }

    suspend fun getAnswerSnapshots(submittedExamId: Long):List<AnswerSnapshot>{
        return studentDao.getSnapshots(submittedExamId)
    }


}