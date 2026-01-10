package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent
import com.example.liveapp.features.streaming.domain.model.YouTubeStreamDetails
import com.example.liveapp.features.streaming.domain.repository.YouTubeRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class CreateLiveEventUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository
) {

    suspend operator fun invoke(
        credential: GoogleAccountCredential,
        event: YouTubeLiveEvent
    ): Result<Pair<YouTubeLiveEvent, YouTubeStreamDetails>> {
        return try {
            // Create the broadcast
            val broadcastResult = youTubeRepository.createLiveEvent(credential, event)
            if (broadcastResult.isFailure) {
                return Result.failure(broadcastResult.exceptionOrNull()!!)
            }

            val broadcast = broadcastResult.getOrNull()!!

            // Create the stream
            val streamResult = youTubeRepository.createLiveStream(credential, event.title)
            if (streamResult.isFailure) {
                return Result.failure(streamResult.exceptionOrNull()!!)
            }

            val stream = streamResult.getOrNull()!!

            // Bind the broadcast to the stream
            val bindResult = youTubeRepository.bindBroadcastToStream(
                credential,
                broadcast.id,
                stream.streamId
            )
            if (bindResult.isFailure) {
                return Result.failure(bindResult.exceptionOrNull()!!)
            }

            // Return the broadcast with stream details
            val updatedBroadcast = broadcast.copy(streamId = stream.streamId)
            Result.success(Pair(updatedBroadcast, stream))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}