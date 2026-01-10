package com.example.liveapp.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scheduled_streams",
    indices = [
        Index(value = ["scheduledTime"]), // Index for time-based queries
        Index(value = ["isNotified"]), // Index for notification status
        Index(value = ["presetId"]) // Index for preset relationships
    ]
)
data class ScheduledStreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val scheduledTime: Long,
    val configJson: String, // JSON string of StreamConfig
    val presetId: Long?,
    val isNotified: Boolean,
    val createdAt: Long
)