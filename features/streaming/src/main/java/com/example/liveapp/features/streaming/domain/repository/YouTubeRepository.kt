package com.example.liveapp.features.streaming.domain.repository

import com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent
import com.example.liveapp.features.streaming.domain.model.YouTubeStreamDetails
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

interface YouTubeRepository {
    suspend fun createLiveEvent(
        credential: GoogleAccountCredential,
        event: YouTubeLiveEvent
    ): Result<YouTubeLiveEvent>

    suspend fun createLiveStream(
        credential: GoogleAccountCredential,
        title: String
    ): Result<YouTubeStreamDetails>

    suspend fun bindBroadcastToStream(
        credential: GoogleAccountCredential,
        broadcastId: String,
        streamId: String
    ): Result<Unit>

    suspend fun updateThumbnail(
        credential: GoogleAccountCredential,
        broadcastId: String,
        thumbnailUri: String
    ): Result<Unit>

    suspend fun getBroadcast(
        credential: GoogleAccountCredential,
        broadcastId: String
    ): Result<YouTubeLiveEvent>
}