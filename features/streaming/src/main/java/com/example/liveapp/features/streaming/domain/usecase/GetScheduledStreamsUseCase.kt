package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.ScheduledStream
import com.example.liveapp.features.streaming.domain.repository.ScheduledStreamRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetScheduledStreamsUseCase @Inject constructor(
    private val repository: ScheduledStreamRepository
) {
    operator fun invoke(): Flow<List<ScheduledStream>> {
        return repository.getAllScheduledStreams()
    }
}