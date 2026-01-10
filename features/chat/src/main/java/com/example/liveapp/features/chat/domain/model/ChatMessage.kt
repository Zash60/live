package com.example.liveapp.features.chat.domain.model

import java.time.Instant

data class ChatMessage(
    val id: String,
    val user: ChatUser,
    val message: String,
    val timestamp: Instant,
    val type: MessageType = MessageType.NORMAL,
    val amount: Double? = null // for superchat or donation
)

enum class MessageType {
    NORMAL,
    SUPER_CHAT,
    DONATION,
    FOLLOWER
}