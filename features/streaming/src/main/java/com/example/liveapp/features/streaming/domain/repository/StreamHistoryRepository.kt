package com.example.liveapp.features.streaming.domain.repository

import com.example.liveapp.features.streaming.domain.model.StreamHistory
import kotlinx.coroutines.flow.Flow

interface StreamHistoryRepository {

    fun getAllStreamHistory(): Flow<List<StreamHistory>>

    suspend fun saveStreamHistory(streamHistory: StreamHistory): Long

    suspend fun getStreamHistoryById(id: Long): StreamHistory?

    suspend fun deleteStreamHistory(id: Long)

    suspend fun getTotalStreamsCount(): Int

    suspend fun getAverageDurationMinutes(): Double?

    suspend fun getPeakViewers(): Int?

    suspend fun getAverageViewers(): Double?
}