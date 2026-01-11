package com.example.liveapp.features.streaming.domain.model

import com.example.liveapp.domain.model.Resolution

data class QualityPreset(
    val name: String,
    val resolution: Resolution,
    val bitrate: Int, // kbps
    val fps: Int
) {
    companion object {
        val LOW = QualityPreset("Low", Resolution.SD_480P, 1000, 24)
        val MEDIUM = QualityPreset("Medium", Resolution.HD_720P, 2000, 30)
        val HIGH = QualityPreset("High", Resolution.FHD_1080P, 4000, 30)
        val ULTRA = QualityPreset("Ultra", Resolution.UHD_4K, 8000, 60)

        val ALL = listOf(LOW, MEDIUM, HIGH, ULTRA)
    }
}
