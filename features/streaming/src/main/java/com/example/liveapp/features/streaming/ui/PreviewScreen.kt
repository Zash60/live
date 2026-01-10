package com.example.liveapp.features.streaming.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.streaming.StreamingViewModel

@Composable
fun PreviewScreen(
    onBack: () -> Unit,
    onStartStreaming: () -> Unit,
    viewModel: StreamingViewModel = hiltViewModel()
) {
    val currentConfig by viewModel.currentConfig.collectAsState()
    val networkStats by viewModel.networkStats.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Preview area (placeholder for actual camera/screen preview)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Preview\n${currentConfig.resolution.width}x${currentConfig.resolution.height}\n${currentConfig.fps}fps",
                color = Color.White,
                style = MaterialTheme.typography.h4
            )
        }

        // Top bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Network stats overlay
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Text(
                text = "Network Stats",
                color = Color.White,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ping: ${networkStats.ping}ms",
                color = Color.White,
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Bitrate: ${networkStats.currentBitrate}kbps",
                color = Color.White,
                style = MaterialTheme.typography.body2
            )
            Text(
                text = "Upload: ${networkStats.uploadSpeed}kbps",
                color = Color.White,
                style = MaterialTheme.typography.body2
            )
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Settings summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Black.copy(alpha = 0.8f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Stream Settings",
                        color = Color.White,
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Resolution: ${currentConfig.resolution.width}x${currentConfig.resolution.height}",
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "Bitrate: ${currentConfig.bitrate}kbps",
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "FPS: ${currentConfig.fps}",
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "Audio: ${if (currentConfig.audioEnabled) "Enabled" else "Disabled"}",
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "Microphone: ${if (currentConfig.microphoneEnabled) "Enabled" else "Disabled"}",
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = "Camera Overlay: ${if (currentConfig.cameraOverlayEnabled) "Enabled" else "Disabled"}",
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            // Start streaming button
            Button(
                onClick = onStartStreaming,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Go Live",
                    color = Color.White,
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}