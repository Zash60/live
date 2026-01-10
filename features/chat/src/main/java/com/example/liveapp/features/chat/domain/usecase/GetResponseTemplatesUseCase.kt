package com.example.liveapp.features.chat.domain.usecase

import com.example.liveapp.features.chat.domain.model.ResponseTemplate
import com.example.liveapp.features.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetResponseTemplatesUseCase(private val chatRepository: ChatRepository) {

    operator fun invoke(): Flow<List<ResponseTemplate>> = chatRepository.getResponseTemplates()

}