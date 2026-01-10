package com.example.liveapp.features.streaming.domain.usecase

import com.example.liveapp.features.streaming.domain.repository.StreamHistoryRepository
import javax.inject.Inject

data class StreamStatistics(
    val totalStreams: Int,
    val averageDurationMinutes: Double?,
    val peakViewers: Int?,
    val averageViewers: Double?
)

class GetStreamStatisticsUseCase @Inject constructor(
    private val repository: StreamHistoryRepository
) {
    suspend operator fun invoke(): StreamStatistics {
        return StreamStatistics(
            totalStreams = repository.getTotalStreamsCount(),
            averageDurationMinutes = repository.getAverageDurationMinutes(),
            peakViewers = repository.getPeakViewers(),
            averageViewers = repository.getAverageViewers()
        )
    }
}