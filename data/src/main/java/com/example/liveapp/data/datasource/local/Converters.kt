package com.example.liveapp.data.datasource.local

import androidx.room.TypeConverter
import com.example.liveapp.domain.model.StreamConfig
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? {
        return value?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun fromStreamConfig(config: StreamConfig?): String? {
        return config?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStreamConfig(json: String?): StreamConfig? {
        return json?.let { gson.fromJson(it, StreamConfig::class.java) }
    }
}