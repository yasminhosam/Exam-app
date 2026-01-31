package com.example.onlineexaminationsystem.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    @ColumnInfo(name = "category_id")
    var categoryId: Long,

    val title:String,
    val duration: Duration,
    val passPercentage:Int,
    val totalScore:Int


    )