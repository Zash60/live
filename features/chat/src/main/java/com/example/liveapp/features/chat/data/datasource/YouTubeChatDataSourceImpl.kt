package com.example.liveapp.features.chat.data.datasource

import com.example.liveapp.features.chat.domain.model.ChatMessage
import com.example.liveapp.features.chat.domain.model.ChatUser
import com.example.liveapp.features.chat.domain.model.MessageType
import com.example.liveapp.features.chat.domain.model.ResponseTemplate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import kotlin.random.Random

class YouTubeChatDataSourceImpl : ChatDataSource {

    private val messages = mutableListOf<ChatMessage>()
    private val blockedUsers = mutableSetOf<String>()

    override fun getComments(): Flow<List<ChatMessage>> = flow {
        emit(messages.filter { !blockedUsers.contains(it.user.id) })
        while (true) {
            delay(2000) // Simulate polling every 2 seconds
            val newMessage = generateMockMessage()
            messages.add(newMessage)
            emit(messages.filter { !blockedUsers.contains(it.user.id) })
        }
    }

    override suspend fun sendMessage(message: String): Result<Unit> {
        // Mock sending
        delay(500)
        return Result.success(Unit)
    }

    override suspend fun blockUser(userId: String): Result<Unit> {
        blockedUsers.add(userId)
        return Result.success(Unit)
    }

    override suspend fun hideMessage(messageId: String): Result<Unit> {
        messages.removeIf { it.id == messageId }
        return Result.success(Unit)
    }

    override fun getResponseTemplates(): Flow<List<ResponseTemplate>> = flow {
        emit(
            listOf(
                ResponseTemplate("1", "Thanks for the message!"),
                ResponseTemplate("2", "Welcome to the stream!"),
                ResponseTemplate("3", "Great question!")
            )
        )
    }

    private fun generateMockMessage(): ChatMessage {
        val users = listOf(
            ChatUser("1", "User1"),
            ChatUser("2", "User2"),
            ChatUser("3", "User3")
        )
        val messages = listOf("Hello!", "Nice stream!", "LOL", "Thanks!")
        val types = listOf(MessageType.NORMAL, MessageType.SUPER_CHAT, MessageType.FOLLOWER, MessageType.DONATION)
        val user = users.random()
        val type = types.random()
        val amount = if (type == MessageType.SUPER_CHAT || type == MessageType.DONATION) Random.nextDouble(1.0, 10.0) else null
        return ChatMessage(
            id = Random.nextInt().toString(),
            user = user,
            message = messages.random(),
            timestamp = Instant.now(),
            type = type,
            amount = amount
        )
    }
}