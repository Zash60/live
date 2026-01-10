package com.example.liveapp.features.chat.data.datasource

import com.example.liveapp.features.chat.domain.model.ChatMessage
import com.example.liveapp.features.chat.domain.model.ResponseTemplate
import kotlinx.coroutines.flow.Flow

interface ChatDataSource {

    fun getComments(): Flow<List<ChatMessage>>

    suspend fun sendMessage(message: String): Result<Unit>

    suspend fun blockUser(userId: String): Result<Unit>

    suspend fun hideMessage(messageId: String): Result<Unit>

    fun getResponseTemplates(): Flow<List<ResponseTemplate>>

}