package com.example.onlineexaminationsystem.data.local


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.data.local.dao.StudentDao
import com.example.onlineexaminationsystem.data.local.dao.UserDao
import com.example.onlineexaminationsystem.data.model.AnswerSnapshot
import com.example.onlineexaminationsystem.data.model.Category
import com.example.onlineexaminationsystem.data.model.Exam
import com.example.onlineexaminationsystem.data.model.Question
import com.example.onlineexaminationsystem.data.model.SubmittedExam
import com.example.onlineexaminationsystem.data.model.User

@Database(
    entities = [Exam::class, User::class,Question::class,SubmittedExam::class,
               AnswerSnapshot::class,Category::class,],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao():UserDao
    abstract fun examDao():ExamDao
    abstract fun studentDao():StudentDao


}