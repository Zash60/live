package com.example.liveapp.features.chat.data.repository

import com.example.liveapp.features.chat.data.datasource.ChatDataSource
import com.example.liveapp.features.chat.domain.model.ChatMessage
import com.example.liveapp.features.chat.domain.model.ResponseTemplate
import com.example.liveapp.features.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(private val chatDataSource: ChatDataSource) : ChatRepository {

    override fun getComments(): Flow<List<ChatMessage>> = chatDataSource.getComments()

    override suspend fun sendReply(message: String): Result<Unit> = chatDataSource.sendMessage(message)

    override suspend fun blockUser(userId: String): Result<Unit> = chatDataSource.blockUser(userId)

    override suspend fun hideMessage(messageId: String): Result<Unit> = chatDataSource.hideMessage(messageId)

    override fun getResponseTemplates(): Flow<List<ResponseTemplate>> = chatDataSource.getResponseTemplates()

}