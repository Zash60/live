package com.example.liveapp.features.chat.di

import com.example.liveapp.features.chat.data.datasource.ChatDataSource
import com.example.liveapp.features.chat.data.datasource.YouTubeChatDataSourceImpl
import com.example.liveapp.features.chat.data.repository.ChatRepositoryImpl
import com.example.liveapp.features.chat.domain.repository.ChatRepository
import com.example.liveapp.features.chat.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatDataSource(): ChatDataSource = YouTubeChatDataSourceImpl()

    @Provides
    @Singleton
    fun provideChatRepository(chatDataSource: ChatDataSource): ChatRepository = ChatRepositoryImpl(chatDataSource)

    @Provides
    fun provideFetchCommentsUseCase(chatRepository: ChatRepository): FetchCommentsUseCase = FetchCommentsUseCase(chatRepository)

    @Provides
    fun provideSendReplyUseCase(chatRepository: ChatRepository): SendReplyUseCase = SendReplyUseCase(chatRepository)

    @Provides
    fun provideBlockUserUseCase(chatRepository: ChatRepository): BlockUserUseCase = BlockUserUseCase(chatRepository)

    @Provides
    fun provideHideMessageUseCase(chatRepository: ChatRepository): HideMessageUseCase = HideMessageUseCase(chatRepository)

    @Provides
    fun provideGetResponseTemplatesUseCase(chatRepository: ChatRepository): GetResponseTemplatesUseCase = GetResponseTemplatesUseCase(chatRepository)

}