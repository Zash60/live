package com.example.liveapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.liveapp.core.NotificationHelper
import com.example.liveapp.features.streaming.worker.ScheduledStreamManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var scheduledStreamManager: ScheduledStreamManager

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        scheduledStreamManager.schedulePeriodicCheck()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
