package com.example.liveapp.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.liveapp.data.datasource.local.dao.ScheduledStreamDao
import com.example.liveapp.data.datasource.local.dao.StreamHistoryDao
import com.example.liveapp.data.datasource.local.dao.StreamPresetDao
import com.example.liveapp.data.datasource.local.entity.ScheduledStreamEntity
import com.example.liveapp.data.datasource.local.entity.StreamHistoryEntity
import com.example.liveapp.data.datasource.local.entity.StreamPresetEntity

@Database(
    entities = [
        StreamHistoryEntity::class,
        StreamPresetEntity::class,
        ScheduledStreamEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StreamingDatabase : RoomDatabase() {

    abstract fun streamHistoryDao(): StreamHistoryDao
    abstract fun streamPresetDao(): StreamPresetDao
    abstract fun scheduledStreamDao(): ScheduledStreamDao

    companion object {
        const val DATABASE_NAME = "streaming_db"
    }
}