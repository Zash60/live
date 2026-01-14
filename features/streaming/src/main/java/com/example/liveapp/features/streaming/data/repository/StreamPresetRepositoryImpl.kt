package com.example.liveapp.features.streaming.data.repository

import com.example.liveapp.data.datasource.local.StreamingDatabase
import com.example.liveapp.data.datasource.local.entity.StreamPresetEntity
import com.example.liveapp.features.streaming.domain.model.StreamPreset
import com.example.liveapp.features.streaming.domain.repository.StreamPresetRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StreamPresetRepositoryImpl @Inject constructor(
    private val database: StreamingDatabase,
    private val gson: Gson = Gson()
) : StreamPresetRepository {

    private val dao = database.streamPresetDao()

    override fun getAllStreamPresets(): Flow<List<StreamPreset>> {
        return dao.getAllStreamPresets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveStreamPreset(preset: StreamPreset): Long {
        val entity = preset.toEntity()
        return dao.insertStreamPreset(entity)
    }

    override suspend fun getStreamPresetById(id: Long): StreamPreset? {
        return dao.getStreamPresetById(id).first()?.toDomain()
    }

    override suspend fun deleteStreamPreset(id: Long) {
        dao.deleteStreamPreset(id)
    }

    override suspend fun updateStreamPreset(preset: StreamPreset) {
        dao.updateStreamPreset(preset.id, preset.name, gson.toJson(preset.config), preset.titleTemplate)
    }

    private fun StreamPresetEntity.toDomain(): StreamPreset {
        val config = gson.fromJson(configJson, com.example.liveapp.domain.model.StreamConfig::class.java)

        return StreamPreset(
            id = id,
            name = name,
            config = config,
            titleTemplate = titleTemplate,
            createdAt = createdAt
        )
    }

    private fun StreamPreset.toEntity(): StreamPresetEntity {
        val configJson = gson.toJson(config)

        return StreamPresetEntity(
            id = id,
            name = name,
            configJson = configJson,
            titleTemplate = titleTemplate,
            createdAt = createdAt
        )
    }
}
