package com.example.liveapp.features.streaming.domain.model

data class YouTubeLiveEvent(
    val id: String = "",
    val title: String,
    val description: String,
    val privacyStatus: PrivacyStatus = PrivacyStatus.PUBLIC,
    val scheduledStartTime: String? = null,
    val categoryId: String = "20", // Gaming category
    val tags: List<String> = listOf("gaming", "gameplay"),
    val thumbnailUrl: String? = null,
    val streamId: String? = null,
    val broadcastStatus: BroadcastStatus = BroadcastStatus.CREATED
)

enum class PrivacyStatus {
    PUBLIC, PRIVATE, UNLISTED
}

enum class BroadcastStatus {
    CREATED, READY, TESTING, LIVE, COMPLETE, REVOKED, TEST_STARTING, LIVE_STARTING
}

data class YouTubeStreamDetails(
    val streamId: String,
    val streamKey: String,
    val ingestionAddress: String,
    val rtmpUrl: String,
    val backupRtmpUrl: String? = null
)

data class YouTubeThumbnail(
    val url: String,
    val width: Int,
    val height: Int
)