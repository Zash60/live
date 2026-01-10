package com.example.liveapp.features.streaming.domain.repository

import com.example.liveapp.features.streaming.domain.model.StreamPreset
import kotlinx.coroutines.flow.Flow

interface StreamPresetRepository {

    fun getAllStreamPresets(): Flow<List<StreamPreset>>

    suspend fun saveStreamPreset(preset: StreamPreset): Long

    suspend fun getStreamPresetById(id: Long): StreamPreset?

    suspend fun deleteStreamPreset(id: Long)

    suspend fun updateStreamPreset(preset: StreamPreset)
}