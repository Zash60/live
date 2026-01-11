package com.example.liveapp.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveapp.data.datasource.local.entity.StreamPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamPresetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStreamPreset(preset: StreamPresetEntity): Long

    @Query("SELECT * FROM stream_presets ORDER BY createdAt DESC")
    fun getAllStreamPresets(): Flow<List<StreamPresetEntity>>

    @Query("SELECT * FROM stream_presets WHERE id = :id")
    fun getStreamPresetById(id: Long): Flow<StreamPresetEntity?>

    @Query("DELETE FROM stream_presets WHERE id = :id")
    fun deleteStreamPreset(id: Long): Int

    @Query("UPDATE stream_presets SET name = :name, configJson = :configJson, titleTemplate = :titleTemplate WHERE id = :id")
    fun updateStreamPreset(id: Long, name: String, configJson: String, titleTemplate: String): Int
}