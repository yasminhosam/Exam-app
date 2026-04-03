package com.example.onlineexaminationsystem.data.mapper

import androidx.compose.runtime.snapshots.Snapshot
import com.example.onlineexaminationsystem.data.remote.AnswerSnapshotDto
import com.example.onlineexaminationsystem.domain.model.Exam
import com.example.onlineexaminationsystem.domain.model.Question
import com.example.onlineexaminationsystem.data.remote.ExamDto
import com.example.onlineexaminationsystem.data.remote.QuestionDto
import com.example.onlineexaminationsystem.data.remote.SubmittedExamDto
import com.example.onlineexaminationsystem.data.remote.UserDto
import com.example.onlineexaminationsystem.domain.model.AnswerSnapshot
import com.example.onlineexaminationsystem.domain.model.SubmittedExam
import com.example.onlineexaminationsystem.domain.model.User
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

// converts the Firestore DTO to Room Entities
fun ExamDto.toRoomEntities(): Pair<Exam, List<Question>> {
    val exam = Exam(
        id = id,
        teacherId = teacherId,
        categoryId = categoryId,
        title = title,
        duration = durationMillis.milliseconds,
        passPercentage = passPercentage,
        totalScore = totalScore,
        isSynced = true,
        isDeleted = false
    )
    val questions = questions.map { dto ->
        Question(
            examId = id,
            text = dto.text,
            options = dto.options,
            correctAnswer = dto.correctAnswer,
            mark = dto.mark,
            isSynced = true
        )
    }
    return Pair(exam, questions)
}

fun Exam.toDto(questions: List<Question>):ExamDto{

    return ExamDto(
        id = id,
        teacherId = teacherId,
        categoryId = categoryId,
        title = title,
        durationMillis = duration.inWholeMilliseconds,
        passPercentage = passPercentage,
        totalScore = totalScore,
        questions =questions.map { it.toDto() }
    )
}

fun Question.toDto():QuestionDto{
    return QuestionDto(
        text = text,
        options = options,
        correctAnswer = correctAnswer,
        mark = mark,

    )
}

fun SubmittedExam.toDto(snapshots: List<AnswerSnapshot>):SubmittedExamDto{
    return SubmittedExamDto(
        id = id,
        examId = examId,
        studentId = studentId,
        studentName = studentName,
        score = score,
        grade = grade,
        status = status.name,
        date = date,
        snapshots = snapshots.map { it.toDto() }
    )
}
fun AnswerSnapshot.toDto(): AnswerSnapshotDto {
    return AnswerSnapshotDto(
        questionText =questionText ,
        options = options,
        correctAnswerIndex = correctAnswerIndex,
        examMark = examMark,
        studentAnswerIndex = studentAnswerIndex

    )
}
fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        email = email,
        role = role.name
    )
}


