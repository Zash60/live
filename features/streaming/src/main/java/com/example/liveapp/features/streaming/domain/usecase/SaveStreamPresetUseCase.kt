package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.StreamPreset
import com.example.liveapp.features.streaming.domain.repository.StreamPresetRepository
import javax.inject.Inject

class SaveStreamPresetUseCase @Inject constructor(
    private val repository: StreamPresetRepository
) {
    suspend operator fun invoke(preset: StreamPreset): Long {
        return repository.saveStreamPreset(preset)
    }
}