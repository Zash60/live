package com.example.liveapp.domain.model

data class StreamConfig(
    val resolution: Resolution = Resolution.HD_720P,
    val bitrate: Int = 2000, // kbps
    val fps: Int = 30,
    val audioEnabled: Boolean = true,
    val microphoneEnabled: Boolean = true,
    val systemAudioEnabled: Boolean = false,
    val cameraOverlayEnabled: Boolean = false,
    val streamUrl: String = "",
    val streamKey: String = "",
    // YouTube Live settings
    val useYouTubeLive: Boolean = false,
    val youTubeEventTitle: String = "",
    val youTubeEventDescription: String = "",
    val youTubePrivacyStatus: YouTubePrivacyStatus = YouTubePrivacyStatus.PUBLIC,
    val youTubeBroadcastId: String? = null,
    val youTubeStreamId: String? = null,
    // Performance settings
    val performanceMode: Boolean = false // Gaming optimization mode
)

enum class YouTubePrivacyStatus {
    PUBLIC, PRIVATE, UNLISTED
}

enum class Resolution(val width: Int, val height: Int) {
    SD_480P(854, 480),
    HD_720P(1280, 720),
    FHD_1080P(1920, 1080),
    UHD_4K(3840, 2160)
}