package com.example.liveapp.features.streaming.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.liveapp.features.streaming.domain.repository.ScheduledStreamRepository
import com.example.liveapp.features.streaming.domain.usecase.StartStreamUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@HiltWorker
class ScheduledStreamWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val scheduledStreamRepository: ScheduledStreamRepository,
    private val startStreamUseCase: StartStreamUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // Check for upcoming scheduled streams within the next 5 minutes
            val upcomingStreams = scheduledStreamRepository.getUpcomingScheduledStreams().first()

            val now = LocalDateTime.now()
            val fiveMinutesLater = now.plusMinutes(5)

            val streamsToNotify = upcomingStreams.filter { stream ->
                val streamTime = stream.scheduledTime
                streamTime.isAfter(now) && streamTime.isBefore(fiveMinutesLater) && !stream.isNotified
            }

            // Send notifications for upcoming streams
            streamsToNotify.forEach { stream ->
                sendNotification(stream.title, "Your stream starts in ${ChronoUnit.MINUTES.between(now, stream.scheduledTime)} minutes")
                scheduledStreamRepository.markAsNotified(stream.id)
            }

            // Check for streams that should start now
            val streamsToStart = upcomingStreams.filter { stream ->
                val streamTime = stream.scheduledTime
                val diff = ChronoUnit.SECONDS.between(now, streamTime)
                diff in -30..30 // Within 30 seconds of scheduled time
            }

            streamsToStart.forEach { stream ->
                try {
                    startStreamUseCase(stream.config)
                    sendNotification(stream.title, "Stream started automatically")
                } catch (e: Exception) {
                    sendNotification(stream.title, "Failed to start scheduled stream: ${e.message}")
                }
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Scheduled Streams",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "scheduled_streams_channel"
    }
}
