package com.example.liveapp.data.di

import com.example.liveapp.data.datasource.UserDataSource
import com.example.liveapp.data.datasource.UserDataSourceImpl
import com.example.liveapp.data.repository.UserRepositoryImpl
import com.example.liveapp.domain.repository.UserRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserDataSource(impl: UserDataSourceImpl): UserDataSource

    companion object {
        @Provides
        @Singleton
        fun provideGson(): Gson = Gson()
    }
}
