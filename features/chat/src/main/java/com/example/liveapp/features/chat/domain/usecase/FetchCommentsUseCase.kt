package com.example.liveapp.features.chat.domain.usecase

import com.example.liveapp.features.chat.domain.model.ChatMessage
import com.example.liveapp.features.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class FetchCommentsUseCase(private val chatRepository: ChatRepository) {

    operator fun invoke(): Flow<List<ChatMessage>> = chatRepository.getComments()

}