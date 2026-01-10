package com.example.liveapp.features.streaming.domain.model

data class NetworkStats(
    val ping: Long = 0, // ms
    val currentBitrate: Int = 0, // kbps
    val droppedFrames: Int = 0,
    val uploadSpeed: Int = 0 // kbps
)