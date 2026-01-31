package com.example.onlineexaminationsystem.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity("submitted_exams",
    foreignKeys = [
        ForeignKey(
            entity = Exam::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exam_id")
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("student_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SubmittedExam(
    @PrimaryKey(true)
    val id:Long=0,
    @ColumnInfo("exam_id")
    val examId:Long,
    @ColumnInfo("student_id")
    val studentId:Long,

    var score:Int,
    val grade:Char,
    val status: Status,
    val date: Long

)

enum class Status{
    PASSED,
    FAILED
}

