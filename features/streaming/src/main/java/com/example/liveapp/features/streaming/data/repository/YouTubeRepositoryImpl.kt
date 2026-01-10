package com.example.liveapp.features.streaming.data.repository

import com.example.liveapp.features.streaming.data.datasource.YouTubeDataSource
import com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent
import com.example.liveapp.features.streaming.domain.model.YouTubeStreamDetails
import com.example.liveapp.features.streaming.domain.repository.YouTubeRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class YouTubeRepositoryImpl @Inject constructor(
    private val youTubeDataSource: YouTubeDataSource
) : YouTubeRepository {

    override suspend fun createLiveEvent(
        credential: GoogleAccountCredential,
        event: YouTubeLiveEvent
    ): Result<YouTubeLiveEvent> {
        return youTubeDataSource.createLiveBroadcast(credential, event)
    }

    override suspend fun createLiveStream(
        credential: GoogleAccountCredential,
        title: String
    ): Result<YouTubeStreamDetails> {
        return youTubeDataSource.createLiveStream(credential, title)
    }

    override suspend fun bindBroadcastToStream(
        credential: GoogleAccountCredential,
        broadcastId: String,
        streamId: String
    ): Result<Unit> {
        return youTubeDataSource.bindBroadcastToStream(credential, broadcastId, streamId)
    }

    override suspend fun updateThumbnail(
        credential: GoogleAccountCredential,
        broadcastId: String,
        thumbnailUri: String
    ): Result<Unit> {
        return youTubeDataSource.updateThumbnail(credential, broadcastId, thumbnailUri)
    }

    override suspend fun getBroadcast(
        credential: GoogleAccountCredential,
        broadcastId: String
    ): Result<YouTubeLiveEvent> {
        return youTubeDataSource.getBroadcast(credential, broadcastId)
    }
}