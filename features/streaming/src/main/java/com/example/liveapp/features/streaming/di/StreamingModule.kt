package com.example.liveapp.features.streaming.di

import com.example.liveapp.data.datasource.local.StreamingDatabase
import com.example.liveapp.features.streaming.data.datasource.YouTubeAuthManager
import com.example.liveapp.features.streaming.data.repository.*
import com.example.liveapp.features.streaming.domain.repository.*
import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StreamingModule {

    @Binds
    @Singleton
    abstract fun bindStreamingRepository(
        impl: StreamingRepositoryImpl
    ): StreamingRepository

    @Binds
    @Singleton
    abstract fun bindYouTubeRepository(
        impl: YouTubeRepositoryImpl
    ): YouTubeRepository

    @Binds
    @Singleton
    abstract fun bindStreamHistoryRepository(
        impl: StreamHistoryRepositoryImpl
    ): StreamHistoryRepository

    @Binds
    @Singleton
    abstract fun bindStreamPresetRepository(
        impl: StreamPresetRepositoryImpl
    ): StreamPresetRepository

    @Binds
    @Singleton
    abstract fun bindScheduledStreamRepository(
        impl: ScheduledStreamRepositoryImpl
    ): ScheduledStreamRepository

    @Provides
    @Singleton
    fun provideYouTubeAuthManager(
        @ApplicationContext context: Context
    ): YouTubeAuthManager {
        return YouTubeAuthManager(context)
    }

    @Provides
    @Singleton
    fun provideStreamingDatabase(
        @ApplicationContext context: Context
    ): StreamingDatabase {
        return Room.databaseBuilder(
            context,
            StreamingDatabase::class.java,
            StreamingDatabase.DATABASE_NAME
        ).build()
    }
}