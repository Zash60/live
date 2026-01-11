package com.example.liveapp.domain.usecase

import com.example.liveapp.domain.model.User
import com.example.liveapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Result<User>> = userRepository.getProfile()
}