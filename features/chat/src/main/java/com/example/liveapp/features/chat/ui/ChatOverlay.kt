package com.example.liveapp.features.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.chat.ChatViewModel

@Composable
fun ChatOverlay(viewModel: ChatViewModel = hiltViewModel()) {
    val messages by viewModel.messages.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Floating chat window
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(300.dp)
                .height(400.dp)
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(8.dp)
        ) {
            LazyColumn {
                items(messages.takeLast(20)) { message -> // Show last 20 messages
                    ChatMessageItem(message)
                }
            }
        }
    }
}