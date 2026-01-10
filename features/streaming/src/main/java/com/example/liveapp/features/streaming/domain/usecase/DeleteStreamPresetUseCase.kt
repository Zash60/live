package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.repository.StreamPresetRepository
import javax.inject.Inject

class DeleteStreamPresetUseCase @Inject constructor(
    private val repository: StreamPresetRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteStreamPreset(id)
    }
}