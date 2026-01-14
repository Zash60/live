package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.domain.model.StreamConfig
import com.example.liveapp.features.streaming.domain.repository.StreamingRepository
import javax.inject.Inject

class StartStreamUseCase @Inject constructor(
private val repository: StreamingRepository
) {
suspend operator fun invoke(config: StreamConfig) = repository.startStream(config)
}
