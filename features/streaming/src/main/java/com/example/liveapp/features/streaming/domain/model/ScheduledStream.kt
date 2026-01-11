package com.example.liveapp.features.streaming.domain.model

import java.time.LocalDateTime
import com.example.liveapp.domain.model.StreamConfig

data class ScheduledStream(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val scheduledTime: LocalDateTime,
    val config: StreamConfig,
    val presetId: Long? = null,
    val isNotified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
