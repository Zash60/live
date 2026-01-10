package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.repository.StreamingRepository
import javax.inject.Inject

class StopStreamUseCase @Inject constructor(
    private val repository: StreamingRepository
) {
    suspend operator fun invoke() = repository.stopStream()
}