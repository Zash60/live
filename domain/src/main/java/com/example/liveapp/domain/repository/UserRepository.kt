package com.example.liveapp.domain.repository

import com.example.liveapp.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user-related operations.
 *
 * This interface defines the contract for user data operations including
 * authentication, profile management, and session handling. Implementations
 * should handle data retrieval from various sources (local cache, remote API).
 */
interface UserRepository {

    /**
     * Initiates user login process.
     *
     * This method starts the authentication flow, typically involving OAuth
     * with Google. The result is emitted as a Flow to handle asynchronous
     * operations and potential errors.
     *
     * @return Flow emitting Result with User on success or exception on failure
     */
    fun login(): Flow<Result<User>>

    /**
     * Retrieves the current user's profile information.
     *
     * Fetches the authenticated user's profile data, which may include
     * updated information from the authentication provider or local cache.
     *
     * @return Flow emitting Result with User on success or exception on failure
     */
    fun getProfile(): Flow<Result<User>>

    /**
     * Logs out the current user.
     *
     * Clears user session data, authentication tokens, and any cached
     * user information. After logout, the user should be redirected to
     * the login screen.
     */
    fun logout()
}