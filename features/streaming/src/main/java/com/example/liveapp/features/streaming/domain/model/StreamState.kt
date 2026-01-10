package com.example.liveapp.features.streaming.domain.model

sealed class StreamState {
    object Idle : StreamState()
    object Preparing : StreamState()
    data class Streaming(val config: StreamConfig, val startTime: Long) : StreamState()
    data class Error(val message: String) : StreamState()
    object Stopped : StreamState()
}