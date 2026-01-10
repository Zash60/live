package com.example.liveapp.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stream_presets")
data class StreamPresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val configJson: String, // JSON string of StreamConfig
    val titleTemplate: String,
    val createdAt: Long
)