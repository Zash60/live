package com.example.liveapp.features.streaming.domain.model

import java.time.LocalDateTime
import com.example.liveapp.domain.model.StreamConfig

data class StreamHistory(
    val id: Long = 0,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val peakViewers: Int,
    val averageViewers: Int,
    val totalViewers: Int,
    val engagementMetrics: EngagementMetrics = EngagementMetrics(),
    val config: StreamConfig
) {
    val duration: Long
        get() = java.time.Duration.between(startTime, endTime).toMinutes()
}

data class EngagementMetrics(
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val follows: Int = 0
)
