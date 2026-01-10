package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.StreamConfig
import com.example.liveapp.features.streaming.domain.repository.StreamingRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val repository: StreamingRepository
) {
    suspend operator fun invoke(config: StreamConfig) = repository.updateConfig(config)
}