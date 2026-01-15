package com.example.liveapp.features.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.domain.model.User
import com.example.liveapp.domain.usecase.GetProfileUseCase
import com.example.liveapp.domain.usecase.LoginUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

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

    fun handleSignInResult(intent: Intent?) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // CORREÇÃO DEFINITIVA: Usamos nossa função awaitTask() personalizada.
                // Isso não bloqueia a thread, apenas suspende a corrotina.
                // O erro de Deadlock desaparecerá.
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                val account = task.awaitTask()
                
                // Se chegou aqui, sucesso!
                login()
            } catch (e: ApiException) {
                _authState.value = AuthState.Error("Google sign in failed: Code ${e.statusCode}")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Login error: ${e.message}")
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

    // Função mágica para converter Task do Google em Coroutine sem bloquear threads
    private suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result -> 
            continuation.resume(result) 
        }
        addOnFailureListener { exception -> 
            continuation.resumeWithException(exception) 
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
