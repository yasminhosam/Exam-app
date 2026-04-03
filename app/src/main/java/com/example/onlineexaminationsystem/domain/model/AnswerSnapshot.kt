package com.example.onlineexaminationsystem.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID.randomUUID

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
    @PrimaryKey(false)
    val id:String=randomUUID().toString(),
    @ColumnInfo("submitted_exam_id")
    val submittedExamId:String,

    val questionText:String,
    val options:List<String>,
    val correctAnswerIndex:Int,
    val examMark:Int,
    val studentAnswerIndex:Int,
    val isSynced:Boolean
)
