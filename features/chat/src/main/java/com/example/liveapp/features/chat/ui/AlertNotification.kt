package com.example.liveapp.features.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.chat.ChatViewModel
import com.example.liveapp.features.chat.domain.model.MessageType

@Composable
fun AlertNotification(viewModel: ChatViewModel = hiltViewModel()) {
    val alerts by viewModel.alerts.collectAsState()

    if (alerts.isNotEmpty()) {
        val latestAlert = alerts.last()
        Box(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.8f))
            ) {
                Text(
                    text = when (latestAlert.type) {
                        MessageType.SUPER_CHAT -> "Super Chat: ${latestAlert.user.name} - $${latestAlert.amount}"
                        MessageType.DONATION -> "Donation: ${latestAlert.user.name} - $${latestAlert.amount}"
                        MessageType.FOLLOWER -> "New Follower: ${latestAlert.user.name}"
                        else -> ""
                    },
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }
        }
    }
}