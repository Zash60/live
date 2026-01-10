package com.example.liveapp.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.liveapp.data.datasource.local.entity.StreamHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreamHistory(streamHistory: StreamHistoryEntity): Long

    @Query("SELECT * FROM stream_history ORDER BY startTime DESC")
    fun getAllStreamHistory(): Flow<List<StreamHistoryEntity>>

    @Query("SELECT * FROM stream_history WHERE id = :id")
    suspend fun getStreamHistoryById(id: Long): StreamHistoryEntity?

    @Query("DELETE FROM stream_history WHERE id = :id")
    suspend fun deleteStreamHistory(id: Long)

    @Query("SELECT COUNT(*) FROM stream_history")
    suspend fun getTotalStreamsCount(): Int

    @Query("SELECT AVG((endTime - startTime) / 60000.0) FROM stream_history")
    suspend fun getAverageDurationMinutes(): Double?

    @Query("SELECT MAX(peakViewers) FROM stream_history")
    suspend fun getPeakViewers(): Int?

    @Query("SELECT AVG(averageViewers) FROM stream_history")
    suspend fun getAverageViewers(): Double?
}