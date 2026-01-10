package com.example.liveapp.features.chat.domain.model

data class ChatUser(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val isBlocked: Boolean = false
)