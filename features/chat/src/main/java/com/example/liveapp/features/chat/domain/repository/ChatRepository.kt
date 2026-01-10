package com.example.liveapp.features.chat.domain.repository

import com.example.liveapp.features.chat.domain.model.ChatMessage
import com.example.liveapp.features.chat.domain.model.ResponseTemplate
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getComments(): Flow<List<ChatMessage>>

    suspend fun sendReply(message: String): Result<Unit>

    suspend fun blockUser(userId: String): Result<Unit>

    suspend fun hideMessage(messageId: String): Result<Unit>

    fun getResponseTemplates(): Flow<List<ResponseTemplate>>

}