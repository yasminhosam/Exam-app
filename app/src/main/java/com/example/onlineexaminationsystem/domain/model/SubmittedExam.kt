package com.example.onlineexaminationsystem.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID.randomUUID

@Entity(
    tableName = "submitted_exams")
data class SubmittedExam(
    @PrimaryKey(autoGenerate = false)
    val id: String = randomUUID().toString(),
    @ColumnInfo(name = "exam_id")
    val examId: String,
    @ColumnInfo(name = "student_id")
    val studentId: String,
    val studentName: String,
    var score: Int,
    val grade: String,
    val status: Status,
    val date: Long,
    val isSynced: Boolean
)

enum class Status {
    PASSED,
    FAILED
}