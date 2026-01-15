package com.example.liveapp.features.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.domain.model.User
import com.example.liveapp.domain.usecase.GetProfileUseCase
import com.example.liveapp.domain.usecase.LoginUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
     * Sets the authentication state to loading and executes the login use case.
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
     * Processa o resultado do Google Sign-In.
     * Executa em IO para evitar travamento da Main Thread (Deadlock).
     */
    fun handleSignInResult(intent: Intent?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // CORREÇÃO: Movemos o processamento para thread de IO
                withContext(Dispatchers.IO) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                    // Isso pode bloquear ou verificar rede, por isso deve ser em IO
                    task.getResult(ApiException::class.java)
                }
                
                // Se não deu erro acima, volta para a Main Thread aqui e prossegue
                login()
            } catch (e: ApiException) {
                _authState.value = AuthState.Error("Google sign in failed: ${e.statusCode}")
            }
        }
    }

    /**
     * Retrieves the current user's profile information.
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
     */
    fun logout() {
        _user.value = null
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
