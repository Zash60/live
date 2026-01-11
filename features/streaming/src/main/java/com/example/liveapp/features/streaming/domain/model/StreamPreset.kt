package com.example.liveapp.features.streaming.domain.model

import com.example.liveapp.domain.model.StreamConfig

data class StreamPreset(
    val id: Long = 0,
    val name: String,
    val config: StreamConfig,
    val titleTemplate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
