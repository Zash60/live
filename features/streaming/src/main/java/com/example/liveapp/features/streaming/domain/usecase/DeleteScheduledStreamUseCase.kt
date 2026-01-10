package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.repository.ScheduledStreamRepository
import javax.inject.Inject

class DeleteScheduledStreamUseCase @Inject constructor(
    private val repository: ScheduledStreamRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteScheduledStream(id)
    }
}