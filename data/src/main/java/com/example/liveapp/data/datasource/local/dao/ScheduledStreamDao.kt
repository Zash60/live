package com.example.liveapp.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.liveapp.data.datasource.local.entity.ScheduledStreamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledStreamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledStream(scheduledStream: ScheduledStreamEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledStreams(scheduledStreams: List<ScheduledStreamEntity>): List<Long>

    @Query("SELECT * FROM scheduled_streams ORDER BY scheduledTime ASC")
    fun getAllScheduledStreams(): Flow<List<ScheduledStreamEntity>>

    @Query("SELECT * FROM scheduled_streams WHERE scheduledTime > :currentTime ORDER BY scheduledTime ASC")
    fun getUpcomingScheduledStreams(currentTime: Long): Flow<List<ScheduledStreamEntity>>

    @Query("SELECT * FROM scheduled_streams WHERE id = :id")
    suspend fun getScheduledStreamById(id: Long): ScheduledStreamEntity?

    @Query("DELETE FROM scheduled_streams WHERE id = :id")
    suspend fun deleteScheduledStream(id: Long): Int

    @Query("UPDATE scheduled_streams SET isNotified = 1 WHERE id = :id")
    suspend fun markAsNotified(id: Long): Int

    @Update
    suspend fun updateScheduledStream(scheduledStream: ScheduledStreamEntity): Int

    @Update
    suspend fun updateScheduledStreams(scheduledStreams: List<ScheduledStreamEntity>): Int

    @Query("DELETE FROM scheduled_streams WHERE id IN (:ids)")
    suspend fun deleteScheduledStreams(ids: List<Long>): Int

    @Query("UPDATE scheduled_streams SET isNotified = 1 WHERE id IN (:ids)")
    suspend fun markAsNotifiedBatch(ids: List<Long>): Int

    @Transaction
    suspend fun insertOrUpdateBatch(scheduledStreams: List<ScheduledStreamEntity>) {
        // First try to insert, then update existing ones
        val ids = insertScheduledStreams(scheduledStreams)
        val entitiesToUpdate = scheduledStreams.filterIndexed { index, _ ->
            ids.getOrNull(index) == -1L // -1L indicates conflict/replacement
        }
        if (entitiesToUpdate.isNotEmpty()) {
            updateScheduledStreams(entitiesToUpdate)
        }
    }
}