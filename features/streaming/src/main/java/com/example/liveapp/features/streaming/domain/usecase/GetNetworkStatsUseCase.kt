package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.model.NetworkStats
import com.example.liveapp.features.streaming.domain.repository.StreamingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNetworkStatsUseCase @Inject constructor(
    private val repository: StreamingRepository
) {
    operator fun invoke(): Flow<NetworkStats> = repository.getNetworkStats()
}