package com.example.liveapp.features.streaming

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.liveapp.MainActivity
import com.example.liveapp.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StreamingService : Service() {

    @Inject
    lateinit var streamingViewModel: StreamingViewModel

    private val CHANNEL_ID = "streaming_channel"
    private val NOTIFICATION_ID = 1

    private var wakeLock: PowerManager.WakeLock? = null
    private var serviceJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
        startHealthMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Start streaming logic here
        streamingViewModel.startStream()

        return START_STICKY
    }

    override fun onDestroy() {
        serviceJob?.cancel()
        streamingViewModel.stopStream()
        releaseWakeLock()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Live Streaming",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live streaming status"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Live Streaming")
            .setContentText("You are currently live streaming")
            .setSmallIcon(android.R.drawable.ic_media_play) // Use system icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW) // Lower priority to save battery
            .build()
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "StreamingService::WakeLock"
        ).apply {
            acquire(10 * 60 * 1000L) // Acquire for 10 minutes, will be renewed
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }

    private fun startHealthMonitoring() {
        serviceJob = serviceScope.launch {
            while (true) {
                // Renew wake lock periodically
                wakeLock?.let { lock ->
                    if (!lock.isHeld) {
                        lock.acquire(10 * 60 * 1000L)
                    }
                }

                // Monitor streaming health and update notification if needed
                updateNotificationWithStats()

                delay(5 * 60 * 1000L) // Check every 5 minutes
            }
        }
    }

    private fun updateNotificationWithStats() {
        // Update notification with current streaming stats
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val updatedNotification = createNotification()
        notificationManager.notify(NOTIFICATION_ID, updatedNotification)
    }
}