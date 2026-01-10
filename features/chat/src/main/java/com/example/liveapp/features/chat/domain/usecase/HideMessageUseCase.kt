package com.example.liveapp.features.chat.domain.usecase

import com.example.liveapp.features.chat.domain.repository.ChatRepository

class HideMessageUseCase(private val chatRepository: ChatRepository) {

    suspend operator fun invoke(messageId: String): Result<Unit> = chatRepository.hideMessage(messageId)

}