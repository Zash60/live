package com.example.liveapp.domain.usecase

import com.example.liveapp.domain.model.User
import com.example.liveapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for handling user login operations.
 *
 * This use case encapsulates the business logic for user authentication,
 * delegating the actual data operations to the UserRepository. It provides
 * a clean interface for the presentation layer to initiate login flows.
 *
 * @property userRepository Repository for user-related data operations
 */
class LoginUseCase constructor(
    private val userRepository: UserRepository
) {

    /**
     * Executes the login operation.
     *
     * Initiates the user authentication process through the repository.
     * The operation is asynchronous and returns a Flow that emits the
     * authentication result.
     *
     * @return Flow emitting Result with authenticated User on success,
     *         or exception on authentication failure
     */
    operator fun invoke(): Flow<Result<User>> = userRepository.login()
}