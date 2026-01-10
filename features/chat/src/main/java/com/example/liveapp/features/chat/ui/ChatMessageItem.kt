package com.example.liveapp.features.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.liveapp.features.chat.domain.model.ChatMessage
import com.example.liveapp.features.chat.domain.model.MessageType

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onBlock: () -> Unit = {},
    onHide: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${message.user.name}: ${message.message}",
                color = when (message.type) {
                    MessageType.SUPER_CHAT -> Color.Yellow
                    MessageType.DONATION -> Color.Green
                    MessageType.FOLLOWER -> Color.Blue
                    else -> Color.White
                }
            )
            if (message.amount != null) {
                Text(text = "$${message.amount}", color = Color.Cyan)
            }
        }
        IconButton(onClick = onBlock) {
            Icon(Icons.Default.Block, contentDescription = "Block user")
        }
        IconButton(onClick = onHide) {
            Icon(Icons.Default.Delete, contentDescription = "Hide message")
        }
    }
}