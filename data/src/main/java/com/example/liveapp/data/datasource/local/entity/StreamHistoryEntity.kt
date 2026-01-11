package com.example.liveapp.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.liveapp.features.streaming.domain.model.StreamConfig

@Entity(
    tableName = "stream_history",
    indices = [
        Index(value = ["startTime"]), // Index for time-based queries
        Index(value = ["endTime"]), // Index for duration calculations
        Index(value = ["peakViewers"]), // Index for performance analytics
        Index(value = ["totalViewers"]) // Index for engagement metrics
    ]
)
data class StreamHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val peakViewers: Int,
    val averageViewers: Int,
    val totalViewers: Int,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val follows: Int,
    val configJson: String // JSON string of StreamConfig
)