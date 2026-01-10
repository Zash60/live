package com.example.liveapp.features.streaming.data.datasource

import android.content.Context
import com.example.liveapp.features.streaming.domain.model.YouTubeApiException
import com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent
import com.example.liveapp.features.streaming.domain.model.YouTubeStreamDetails
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class YouTubeDataSource @Inject constructor(
    private val context: Context
) {

    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    private fun getYouTubeService(credential: GoogleAccountCredential): YouTube {
        return YouTube.Builder(transport, jsonFactory, credential)
            .setApplicationName("LiveApp")
            .build()
    }

    suspend fun createLiveBroadcast(
        credential: GoogleAccountCredential,
        event: YouTubeLiveEvent
    ): Result<YouTubeLiveEvent> = withContext(Dispatchers.IO) {
        try {
            val youtube = getYouTubeService(credential)

            // Create broadcast
            val broadcast = LiveBroadcast().apply {
                snippet = LiveBroadcastSnippet().apply {
                    title = event.title
                    description = event.description
                    scheduledStartTime = event.scheduledStartTime?.let {
                        com.google.api.client.util.DateTime(it)
                    }
                    tags = event.tags
                }
                status = LiveBroadcastStatus().apply {
                    privacyStatus = event.privacyStatus.name.lowercase()
                }
                contentDetails = LiveBroadcastContentDetails().apply {
                    enableAutoStart = false
                    enableAutoStop = false
                    enableClosedCaptions = false
                    enableDvr = true
                    enableEmbed = true
                    recordFromStart = true
                    startWithSlate = false
                }
            }

            val request = youtube.liveBroadcasts().insert(
                listOf("snippet", "status", "contentDetails"),
                broadcast
            )

            val response = request.execute()

            val createdEvent = YouTubeLiveEvent(
                id = response.id,
                title = response.snippet.title,
                description = response.snippet.description ?: "",
                privacyStatus = YouTubeLiveEvent.PrivacyStatus.valueOf(
                    response.status.privacyStatus.uppercase()
                ),
                scheduledStartTime = response.snippet.scheduledStartTime?.toStringRfc3339(),
                categoryId = response.snippet.categoryId ?: "20",
                tags = response.snippet.tags ?: emptyList(),
                broadcastStatus = YouTubeLiveEvent.BroadcastStatus.valueOf(
                    response.status.lifeCycleStatus.uppercase()
                )
            )

            Result.success(createdEvent)
        } catch (e: GoogleJsonResponseException) {
            Result.failure(mapGoogleJsonException(e))
        } catch (e: IOException) {
            Result.failure(YouTubeApiException.NetworkException("Network error", e))
        } catch (e: Exception) {
            Result.failure(YouTubeApiException.UnknownException("Unexpected error", e))
        }
    }

    suspend fun createLiveStream(
        credential: GoogleAccountCredential,
        title: String
    ): Result<YouTubeStreamDetails> = withContext(Dispatchers.IO) {
        try {
            val youtube = getYouTubeService(credential)

            val stream = LiveStream().apply {
                snippet = LiveStreamSnippet().apply {
                    this.title = title
                }
                cdn = CdnSettings().apply {
                    ingestionType = "rtmp"
                    resolution = "variable"
                    frameRate = "variable"
                }
            }

            val request = youtube.liveStreams().insert(
                listOf("snippet", "cdn", "status"),
                stream
            )

            val response = request.execute()

            val streamDetails = YouTubeStreamDetails(
                streamId = response.id,
                streamKey = response.cdn.ingestionInfo.streamName,
                ingestionAddress = response.cdn.ingestionInfo.ingestionAddress,
                rtmpUrl = "${response.cdn.ingestionInfo.ingestionAddress}/${response.cdn.ingestionInfo.streamName}",
                backupRtmpUrl = response.cdn.ingestionInfo.backupIngestionAddress?.let {
                    "$it/${response.cdn.ingestionInfo.streamName}"
                }
            )

            Result.success(streamDetails)
        } catch (e: GoogleJsonResponseException) {
            Result.failure(mapGoogleJsonException(e))
        } catch (e: IOException) {
            Result.failure(YouTubeApiException.NetworkException("Network error", e))
        } catch (e: Exception) {
            Result.failure(YouTubeApiException.UnknownException("Unexpected error", e))
        }
    }

    suspend fun bindBroadcastToStream(
        credential: GoogleAccountCredential,
        broadcastId: String,
        streamId: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val youtube = getYouTubeService(credential)

            val request = youtube.liveBroadcasts().bind(broadcastId, streamId)
            request.execute()

            Result.success(Unit)
        } catch (e: GoogleJsonResponseException) {
            Result.failure(mapGoogleJsonException(e))
        } catch (e: IOException) {
            Result.failure(YouTubeApiException.NetworkException("Network error", e))
        } catch (e: Exception) {
            Result.failure(YouTubeApiException.UnknownException("Unexpected error", e))
        }
    }

    suspend fun updateThumbnail(
        credential: GoogleAccountCredential,
        broadcastId: String,
        thumbnailUri: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val youtube = getYouTubeService(credential)

            val content = context.contentResolver.openInputStream(android.net.Uri.parse(thumbnailUri))
                ?: return Result.failure(IOException("Cannot open thumbnail file"))

            val request = youtube.thumbnails().set(broadcastId)
            request.mediaHttpUploader.isDirectUploadEnabled = false
            request.mediaHttpUploader.chunkSize = 1024 * 1024 // 1MB chunks
            request.mediaHttpUploader.mediaContent = com.google.api.client.http.InputStreamContent(
                "image/jpeg",
                content
            )

            request.execute()

            Result.success(Unit)
        } catch (e: GoogleJsonResponseException) {
            Result.failure(mapGoogleJsonException(e))
        } catch (e: IOException) {
            Result.failure(YouTubeApiException.NetworkException("Network error", e))
        } catch (e: Exception) {
            Result.failure(YouTubeApiException.UnknownException("Unexpected error", e))
        }
    }

    suspend fun getBroadcast(
        credential: GoogleAccountCredential,
        broadcastId: String
    ): Result<YouTubeLiveEvent> = withContext(Dispatchers.IO) {
        try {
            val youtube = getYouTubeService(credential)

            val request = youtube.liveBroadcasts().list(listOf("snippet", "status", "contentDetails"))
            request.id = broadcastId

            val response = request.execute()
            val broadcast = response.items.firstOrNull()
                ?: return Result.failure(IOException("Broadcast not found"))

            val event = YouTubeLiveEvent(
                id = broadcast.id,
                title = broadcast.snippet.title,
                description = broadcast.snippet.description ?: "",
                privacyStatus = YouTubeLiveEvent.PrivacyStatus.valueOf(
                    broadcast.status.privacyStatus.uppercase()
                ),
                scheduledStartTime = broadcast.snippet.scheduledStartTime?.toStringRfc3339(),
                categoryId = broadcast.snippet.categoryId ?: "20",
                tags = broadcast.snippet.tags ?: emptyList(),
                broadcastStatus = YouTubeLiveEvent.BroadcastStatus.valueOf(
                    broadcast.status.lifeCycleStatus.uppercase()
                )
            )

            Result.success(event)
        } catch (e: GoogleJsonResponseException) {
            Result.failure(mapGoogleJsonException(e))
        } catch (e: IOException) {
            Result.failure(YouTubeApiException.NetworkException("Network error", e))
        } catch (e: Exception) {
            Result.failure(YouTubeApiException.UnknownException("Unexpected error", e))
        }
    }

    private fun mapGoogleJsonException(e: GoogleJsonResponseException): YouTubeApiException {
        return when (e.details?.code) {
            401 -> YouTubeApiException.AuthenticationException("Authentication required", e)
            403 -> {
                if (e.details?.message?.contains("quota", ignoreCase = true) == true) {
                    YouTubeApiException.ApiLimitExceededException("API quota exceeded", e)
                } else {
                    YouTubeApiException.AuthenticationException("Access denied", e)
                }
            }
            400, 404 -> YouTubeApiException.InvalidRequestException("Invalid request", e)
            else -> YouTubeApiException.UnknownException("YouTube API error: ${e.details?.message}", e)
        }
    }
}