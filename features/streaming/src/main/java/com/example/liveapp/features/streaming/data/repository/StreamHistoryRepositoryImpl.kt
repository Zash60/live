package com.example.liveapp.features.streaming.data.repository

import com.example.liveapp.data.datasource.local.StreamingDatabase
import com.example.liveapp.data.datasource.local.entity.StreamHistoryEntity
import com.example.liveapp.features.streaming.domain.model.EngagementMetrics
import com.example.liveapp.features.streaming.domain.model.StreamHistory
import com.example.liveapp.features.streaming.domain.repository.StreamHistoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class StreamHistoryRepositoryImpl @Inject constructor(
    private val database: StreamingDatabase,
    private val gson: Gson = Gson()
) : StreamHistoryRepository {

    private val dao = database.streamHistoryDao()

    override fun getAllStreamHistory(): Flow<List<StreamHistory>> {
        return dao.getAllStreamHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveStreamHistory(streamHistory: StreamHistory): Long {
        val entity = streamHistory.toEntity()
        return dao.insertStreamHistory(entity)
    }

    override suspend fun getStreamHistoryById(id: Long): StreamHistory? {
        return dao.getStreamHistoryById(id)?.toDomain()
    }

    override suspend fun deleteStreamHistory(id: Long) {
        dao.deleteStreamHistory(id)
    }

    override suspend fun getTotalStreamsCount(): Int {
        return dao.getTotalStreamsCount()
    }

    override suspend fun getAverageDurationMinutes(): Double? {
        return dao.getAverageDurationMinutes()
    }

    override suspend fun getPeakViewers(): Int? {
        return dao.getPeakViewers()
    }

    override suspend fun getAverageViewers(): Double? {
        return dao.getAverageViewers()
    }

    private fun StreamHistoryEntity.toDomain(): StreamHistory {
        val config = gson.fromJson(configJson, com.example.liveapp.features.streaming.domain.model.StreamConfig::class.java)
        val startTime = LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.UTC)
        val endTime = LocalDateTime.ofEpochSecond(endTime, 0, ZoneOffset.UTC)
        val engagementMetrics = EngagementMetrics(likes, comments, shares, follows)

        return StreamHistory(
            id = id,
            title = title,
            startTime = startTime,
            endTime = endTime,
            peakViewers = peakViewers,
            averageViewers = averageViewers,
            totalViewers = totalViewers,
            engagementMetrics = engagementMetrics,
            config = config
        )
    }

    private fun StreamHistory.toEntity(): StreamHistoryEntity {
        val configJson = gson.toJson(config)

        return StreamHistoryEntity(
            id = id,
            title = title,
            startTime = startTime.toEpochSecond(ZoneOffset.UTC),
            endTime = endTime.toEpochSecond(ZoneOffset.UTC),
            peakViewers = peakViewers,
            averageViewers = averageViewers,
            totalViewers = totalViewers,
            likes = engagementMetrics.likes,
            comments = engagementMetrics.comments,
            shares = engagementMetrics.shares,
            follows = engagementMetrics.follows,
            configJson = configJson
        )
    }
}