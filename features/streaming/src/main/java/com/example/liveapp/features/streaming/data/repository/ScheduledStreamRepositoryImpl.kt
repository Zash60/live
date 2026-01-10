package com.example.liveapp.features.streaming.data.repository

import com.example.liveapp.data.datasource.local.StreamingDatabase
import com.example.liveapp.data.datasource.local.entity.ScheduledStreamEntity
import com.example.liveapp.features.streaming.domain.model.ScheduledStream
import com.example.liveapp.features.streaming.domain.repository.ScheduledStreamRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class ScheduledStreamRepositoryImpl @Inject constructor(
    private val database: StreamingDatabase,
    private val gson: Gson = Gson()
) : ScheduledStreamRepository {

    private val dao = database.scheduledStreamDao()

    override fun getAllScheduledStreams(): Flow<List<ScheduledStream>> {
        return dao.getAllScheduledStreams().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUpcomingScheduledStreams(): Flow<List<ScheduledStream>> {
        val currentTime = System.currentTimeMillis() / 1000
        return dao.getUpcomingScheduledStreams(currentTime).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveScheduledStream(scheduledStream: ScheduledStream): Long {
        val entity = scheduledStream.toEntity()
        return dao.insertScheduledStream(entity)
    }

    override suspend fun getScheduledStreamById(id: Long): ScheduledStream? {
        return dao.getScheduledStreamById(id)?.toDomain()
    }

    override suspend fun deleteScheduledStream(id: Long) {
        dao.deleteScheduledStream(id)
    }

    override suspend fun markAsNotified(id: Long) {
        dao.markAsNotified(id)
    }

    suspend fun saveScheduledStreamsBatch(scheduledStreams: List<ScheduledStream>): List<Long> {
        val entities = scheduledStreams.map { it.toEntity() }
        return dao.insertScheduledStreams(entities)
    }

    suspend fun deleteScheduledStreamsBatch(ids: List<Long>) {
        dao.deleteScheduledStreams(ids)
    }

    suspend fun markAsNotifiedBatch(ids: List<Long>) {
        dao.markAsNotifiedBatch(ids)
    }

    private fun ScheduledStreamEntity.toDomain(): ScheduledStream {
        val config = gson.fromJson(configJson, com.example.liveapp.features.streaming.domain.model.StreamConfig::class.java)
        val scheduledTime = LocalDateTime.ofEpochSecond(scheduledTime, 0, ZoneOffset.UTC)

        return ScheduledStream(
            id = id,
            title = title,
            description = description,
            scheduledTime = scheduledTime,
            config = config,
            presetId = presetId,
            isNotified = isNotified,
            createdAt = createdAt
        )
    }

    private fun ScheduledStream.toEntity(): ScheduledStreamEntity {
        val configJson = gson.toJson(config)

        return ScheduledStreamEntity(
            id = id,
            title = title,
            description = description,
            scheduledTime = scheduledTime.toEpochSecond(ZoneOffset.UTC),
            configJson = configJson,
            presetId = presetId,
            isNotified = isNotified,
            createdAt = createdAt
        )
    }
}