package com.example.liveapp.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.domain.model.User
import com.example.liveapp.domain.usecase.GetProfileUseCase
import com.example.liveapp.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling authentication-related UI state and business logic.
 *
 * This ViewModel manages the authentication flow including login, profile retrieval,
 * and logout operations. It exposes state flows for the UI to observe authentication
 * status and user information.
 *
 * @property loginUseCase Use case for handling login operations
 * @property getProfileUseCase Use case for retrieving user profile information
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    /**
     * Current authentication state exposed to the UI.
     * Observers can react to state changes for UI updates.
     */
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    /**
     * Current authenticated user information.
     * Null when user is not authenticated.
     */
    val user: StateFlow<User?> = _user.asStateFlow()

    /**
     * Initiates the user login process.
     *
     * Sets the authentication state to loading and executes the login use case.
     * Updates the state and user information based on the result.
     */
    fun login() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            loginUseCase().collect { result ->
                result.fold(
                    onSuccess = { user ->
                        _user.value = user
                        _authState.value = AuthState.Success
                    },
                    onFailure = { error ->
                        _authState.value = AuthState.Error(error.message ?: "Login failed")
                    }
                )
            }
        }
    }

    /**
     * Retrieves the current user's profile information.
     *
     * Fetches updated profile data for the authenticated user.
     * Useful for refreshing user information after login or periodically.
     */
    fun getProfile() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            getProfileUseCase().collect { result ->
                result.fold(
                    onSuccess = { user ->
                        _user.value = user
                        _authState.value = AuthState.Success
                    },
                    onFailure = { error ->
                        _authState.value = AuthState.Error(error.message ?: "Failed to get profile")
                    }
                )
            }
        }
    }

    /**
     * Logs out the current user.
     *
     * Clears user data and resets authentication state to idle.
     * Should be called when the user explicitly logs out or when session expires.
     */
    fun logout() {
        _user.value = null
        _authState.value = AuthState.Idle
    }
}

/**
 * Sealed class representing the various states of authentication operations.
 *
 * Used by the UI to determine what to display and how to handle user interactions
 * during different phases of the authentication process.
 */
sealed class AuthState {
    /** Initial state when no authentication operation is in progress */
    object Idle : AuthState()

    /** State indicating an authentication operation is currently in progress */
    object Loading : AuthState()

    /** State indicating a successful authentication operation */
    object Success : AuthState()

    /**
     * State indicating an authentication operation failed
     * @property message Human-readable error message describing the failure
     */
    data class Error(val message: String) : AuthState()
}