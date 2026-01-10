package com.example.liveapp.features.streaming.domain.repository

import com.example.liveapp.features.streaming.domain.model.ScheduledStream
import kotlinx.coroutines.flow.Flow

interface ScheduledStreamRepository {

    fun getAllScheduledStreams(): Flow<List<ScheduledStream>>

    fun getUpcomingScheduledStreams(): Flow<List<ScheduledStream>>

    suspend fun saveScheduledStream(scheduledStream: ScheduledStream): Long

    suspend fun getScheduledStreamById(id: Long): ScheduledStream?

    suspend fun deleteScheduledStream(id: Long)

    suspend fun markAsNotified(id: Long)
}