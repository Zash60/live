package com.example.liveapp.features.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.features.chat.domain.model.ChatMessage
import com.example.liveapp.features.chat.domain.model.MessageType
import com.example.liveapp.features.chat.domain.model.ResponseTemplate
import com.example.liveapp.features.chat.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val fetchCommentsUseCase: FetchCommentsUseCase,
    private val sendReplyUseCase: SendReplyUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val hideMessageUseCase: HideMessageUseCase,
    private val getResponseTemplatesUseCase: GetResponseTemplatesUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _templates = MutableStateFlow<List<ResponseTemplate>>(emptyList())
    val templates: StateFlow<List<ResponseTemplate>> = _templates

    private val _alerts = MutableStateFlow<List<ChatMessage>>(emptyList()) // for notifications
    val alerts: StateFlow<List<ChatMessage>> = _alerts

    init {
        viewModelScope.launch {
            fetchCommentsUseCase().collect { messages ->
                _messages.value = messages
                // Filter alerts
                _alerts.value = messages.filter { it.type != MessageType.NORMAL }
            }
        }
        viewModelScope.launch {
            getResponseTemplatesUseCase().collect { templates ->
                _templates.value = templates
            }
        }
    }

    fun sendReply(message: String) {
        viewModelScope.launch {
            sendReplyUseCase(message)
        }
    }

    fun blockUser(userId: String) {
        viewModelScope.launch {
            blockUserUseCase(userId)
        }
    }

    fun hideMessage(messageId: String) {
        viewModelScope.launch {
            hideMessageUseCase(messageId)
        }
    }
}