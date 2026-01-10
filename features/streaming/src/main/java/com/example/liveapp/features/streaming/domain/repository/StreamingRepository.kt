package com.example.liveapp.features.streaming.domain.repository

import com.example.liveapp.features.streaming.domain.model.NetworkStats
import com.example.liveapp.features.streaming.domain.model.StreamConfig
import com.example.liveapp.features.streaming.domain.model.StreamState
import kotlinx.coroutines.flow.Flow

interface StreamingRepository {
    fun getStreamState(): Flow<StreamState>
    suspend fun startStream(config: StreamConfig): Result<Unit>
    suspend fun stopStream(): Result<Unit>
    suspend fun updateConfig(config: StreamConfig): Result<Unit>
    fun getNetworkStats(): Flow<NetworkStats>
}