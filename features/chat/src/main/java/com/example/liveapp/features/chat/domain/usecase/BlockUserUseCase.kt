package com.example.liveapp.features.chat.domain.usecase

import com.example.liveapp.features.chat.domain.repository.ChatRepository

class BlockUserUseCase(private val chatRepository: ChatRepository) {

    suspend operator fun invoke(userId: String): Result<Unit> = chatRepository.blockUser(userId)

}