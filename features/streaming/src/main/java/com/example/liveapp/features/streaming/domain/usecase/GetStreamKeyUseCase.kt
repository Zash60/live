package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.YouTubeStreamDetails
import com.example.liveapp.features.streaming.domain.repository.YouTubeRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class GetStreamKeyUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository
) {

    suspend operator fun invoke(
        credential: GoogleAccountCredential,
        broadcastId: String
    ): Result<YouTubeStreamDetails> {
        return try {
            // Get the broadcast to find the associated stream ID
            val broadcastResult = youTubeRepository.getBroadcast(credential, broadcastId)
            if (broadcastResult.isFailure) {
                return Result.failure(broadcastResult.exceptionOrNull()!!)
            }

            val broadcast = broadcastResult.getOrNull()!!
            val streamId = broadcast.streamId
                ?: return Result.failure(IllegalStateException("Broadcast not bound to a stream"))

            // For now, we'll need to create a new stream or assume we have the stream details
            // In a real implementation, you might want to store stream details or query them
            // Since YouTube API doesn't directly give stream key from broadcast,
            // we'll return a placeholder that indicates we need to create/retrieve stream details

            // This is a simplified implementation - in practice, you'd store stream details
            // when creating the event and retrieve them here
            Result.failure(NotImplementedError("Stream key retrieval needs to be implemented based on stored stream details"))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}