package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.repository.YouTubeRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class UpdateThumbnailUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository
) {

    suspend operator fun invoke(
        credential: GoogleAccountCredential,
        broadcastId: String,
        thumbnailUri: String
    ): Result<Unit> {
        return youTubeRepository.updateThumbnail(credential, broadcastId, thumbnailUri)
    }
}