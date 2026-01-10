package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.StreamHistory
import com.example.liveapp.features.streaming.domain.repository.StreamHistoryRepository
import javax.inject.Inject

class SaveStreamHistoryUseCase @Inject constructor(
    private val repository: StreamHistoryRepository
) {
    suspend operator fun invoke(streamHistory: StreamHistory): Long {
        return repository.saveStreamHistory(streamHistory)
    }
}