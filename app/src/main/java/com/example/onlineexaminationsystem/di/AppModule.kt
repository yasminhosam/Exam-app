package com.example.onlineexaminationsystem.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.onlineexaminationsystem.R
import com.example.onlineexaminationsystem.data.local.AppDatabase
import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.data.local.dao.StudentDao
import com.example.onlineexaminationsystem.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context):AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "online_exam_db",
        )
            // runs only once when the app is installed and the database is created for the first time
            .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)


                db.execSQL("""
                    INSERT INTO users (name, email, password, role) 
                    VALUES ('Admin', 'admin@gmail.com', 'admin123', 'ADMIN')
                """)

                db.execSQL("""
                    INSERT INTO categories (name, imageRes) VALUES 
                    ('Science', ${R.drawable.ic_science}),
                    ('Math', ${R.drawable.ic_math}),
                    ('Programming', ${R.drawable.ic_programming}),
                    ('Geography', ${R.drawable.geography}),
                    ('Physics', ${R.drawable.physics}),
                    ('Biology', ${R.drawable.ic_biology}),
                    ('Art', ${R.drawable.ic_art}),
                    ('History', ${R.drawable.ic_history})
                """)
                // 3. Insert Exam (Linked to Category 'Programming' which is roughly ID 3)
                // - category_id: 3
                // - duration: 30 minutes = 1,800,000 milliseconds
                // - passPercentage: 50
                // - totalScore: 10
                db.execSQL("""
                    INSERT INTO exams (category_id, title, duration, passPercentage, totalScore) 
                    VALUES (3, 'Android Basics', 60000, 50, 10)
                """)

                // 4. Insert Questions (Linked to Exam ID 1, which is 'Android Basics')
                // - exam_id: 1 (Matches the exam inserted above)
                // - options: JSON Array String
                db.execSQL("""
                    INSERT INTO questions (exam_id, text, options, correctAnswer, mark) 
                    VALUES 
                    (1, 'What is the base class for Layouts?', '["View", "ViewGroup", "Context"]', 1, 5),
                    (1, 'Which language is used for Android?', '["Kotlin", "Swift", "Python"]', 0, 5)
                """)


            }
        })
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db:AppDatabase):UserDao{
        return db.userDao()
    }
    @Provides
    @Singleton
    fun provideExamDao(db: AppDatabase):ExamDao{
        return db.examDao()
    }

    @Provides
    @Singleton
    fun provideStudentDao(db:AppDatabase):StudentDao{
        return db.studentDao()
    }

}