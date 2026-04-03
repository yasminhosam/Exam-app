package com.example.onlineexaminationsystem.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID.randomUUID
import kotlin.time.Duration

@Entity(
    tableName = "exams",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("category_id")
        )
    ]
)
data class Exam(
    @PrimaryKey(autoGenerate = false)
    val id:String=randomUUID().toString(),
    @ColumnInfo(name = "category_id")
    var categoryId: String,
    @ColumnInfo(name = "teacher_id")
    val teacherId: String = "",
    val title:String,
    val duration: Duration,
    val passPercentage:Int,
    val totalScore:Int,
    val isSynced:Boolean,
    val isDeleted:Boolean


    )