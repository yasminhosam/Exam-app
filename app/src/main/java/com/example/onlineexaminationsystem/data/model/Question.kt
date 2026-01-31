package com.example.onlineexaminationsystem.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity("questions",
    foreignKeys = [
        ForeignKey(
            entity = Exam::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("exam_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class Question(
    @PrimaryKey(true)
    val id:Long=0,
    @ColumnInfo("exam_id")
    val examId:Long,
    val text:String,
    val options:List<String>,
    val correctAnswer:Int,
    val mark:Int
)
