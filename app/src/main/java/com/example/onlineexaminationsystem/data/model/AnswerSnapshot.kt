package com.example.onlineexaminationsystem.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity("answer_snapshots",
    foreignKeys = [
        ForeignKey(
            entity = SubmittedExam::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("submitted_exam_id")
        )
    ]
    )
data class AnswerSnapshot(
    @PrimaryKey(true)
    val id:Long=0,
    @ColumnInfo("submitted_exam_id")
    val submittedExamId:Long,

    val questionText:String,
    val options:List<String>,
    val correctAnswerIndex:Int,
    val examMark:Int,
    val studentAnswerIndex:Int
)
