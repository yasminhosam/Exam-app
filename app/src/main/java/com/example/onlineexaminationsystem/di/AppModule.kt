package com.example.onlineexaminationsystem.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import com.example.onlineexaminationsystem.R
import com.example.onlineexaminationsystem.data.local.AppDatabase
import com.example.onlineexaminationsystem.data.local.dao.ExamDao
import com.example.onlineexaminationsystem.data.local.dao.StudentDao
import com.google.ai.client.generativeai.GenerativeModel

import com.example.onlineexaminationsystem.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
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
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "online_exam_db",
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    // We use onOpen and insert with "OR IGNORE" to handle
                    // both first-time creation and migrations safely.
                    insertInitialCategories(db)
                }

                private fun insertInitialCategories(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
    INSERT OR IGNORE INTO categories (id, name, imageRes) VALUES 
    ('cat_sci', 'Science', 'ic_science'),
    ('cat_math', 'Math', 'ic_math'),
    ('cat_prog', 'Programming', 'ic_programming'),
    ('cat_geo', 'Geography', 'geography'),
    ('cat_phys', 'Physics', 'physics'),
    ('cat_bio', 'Biology', 'ic_biology'),
    ('cat_art', 'Art', 'ic_art'),
    ('cat_hist', 'History', 'ic_history')
"""
                    )
                }
            })
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @Singleton
    fun provideExamDao(db: AppDatabase): ExamDao {
        return db.examDao()
    }

    @Provides
    @Singleton
    fun provideStudentDao(db: AppDatabase): StudentDao {
        return db.studentDao()
    }

    @Provides
    @Singleton
    fun provideWorkerManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    @Provides
    @Singleton
    fun provideGson():Gson{
        return Gson()
    }
    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        val currentKey = BuildConfig.GEMINI_API_KEY

        Log.d("ApiDebug", "The injected Key is: [$currentKey]")

        return GenerativeModel(
            modelName = "gemini-3.1-flash-lite-preview",
            apiKey = currentKey
        )
    }

}