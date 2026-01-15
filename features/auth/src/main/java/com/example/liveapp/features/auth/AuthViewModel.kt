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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    /**
     * Tenta realizar o login (usado quando já temos a conta ou após o retorno da Intent)
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
     * Processa o resultado da Activity de Login do Google
     */
    fun handleSignInResult(intent: Intent?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Tenta pegar a conta logada do resultado
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                // Se falhar aqui, vai para o catch
                task.getResult(ApiException::class.java)
                
                // Se deu certo, chama o fluxo de login normal para atualizar o User e State
                login()
            } catch (e: ApiException) {
                _authState.value = AuthState.Error("Google sign in failed: Code ${e.statusCode}")
            }
        }
    }

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
