package com.example.liveapp.features.chat.domain.usecase

import com.example.liveapp.features.chat.domain.repository.ChatRepository

class SendReplyUseCase(private val chatRepository: ChatRepository) {

    suspend operator fun invoke(message: String): Result<Unit> = chatRepository.sendReply(message)

}