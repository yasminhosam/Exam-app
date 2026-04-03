package com.example.onlineexaminationsystem.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.data.local.dao.StudentDao
import com.example.onlineexaminationsystem.domain.model.AnswerSnapshot
import com.example.onlineexaminationsystem.domain.model.Category
import com.example.onlineexaminationsystem.domain.model.Exam
import com.example.onlineexaminationsystem.domain.model.Question
import com.example.onlineexaminationsystem.domain.model.SubmittedExam

@Database(
    entities = [
        Exam::class, Question::class, SubmittedExam::class,
               AnswerSnapshot::class, Category::class,],
    version = 4
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun examDao():ExamDao
    abstract fun studentDao():StudentDao


}