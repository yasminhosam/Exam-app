package com.example.onlineexaminationsystem.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID.randomUUID

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
    @PrimaryKey(false)
    val id:String=randomUUID().toString(),
    @ColumnInfo("exam_id")
    val examId:String,
    val text:String,
    val options:List<String>,
    val correctAnswer:Int,
    val mark:Int,
    val isSynced:Boolean
)
