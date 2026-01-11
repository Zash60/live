package com.example.liveapp.features.streaming.domain.model

import com.example.liveapp.domain.model.StreamConfig

sealed class StreamState {
    object Idle : StreamState()
    object Preparing : StreamState()
    data class Streaming(val config: StreamConfig, val startTime: Long) : StreamState()
    data class Error(val message: String) : StreamState()
    object Stopped : StreamState()
}