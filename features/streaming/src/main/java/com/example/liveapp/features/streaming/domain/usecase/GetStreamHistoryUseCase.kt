package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.StreamHistory
import com.example.liveapp.features.streaming.domain.repository.StreamHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStreamHistoryUseCase @Inject constructor(
    private val repository: StreamHistoryRepository
) {
    operator fun invoke(): Flow<List<StreamHistory>> {
        return repository.getAllStreamHistory()
    }
}