package com.example.onlineexaminationsystem

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.onlineexaminationsystem.domain.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MyApplication:Application(), Configuration.Provider {


    @Inject
    lateinit var workerFactory:HiltWorkerFactory
    override val workManagerConfiguration: Configuration
        get() =Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


}