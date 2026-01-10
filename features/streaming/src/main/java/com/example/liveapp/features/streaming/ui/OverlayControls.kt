package com.example.liveapp.features.streaming.ui

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.liveapp.features.chat.ui.AlertNotification
import com.example.liveapp.features.chat.ui.ChatOverlay
import com.example.liveapp.features.streaming.StreamingNavigation.FULL_CHAT
import com.example.liveapp.features.streaming.StreamingViewModel
import com.example.liveapp.features.streaming.domain.model.StreamState

private fun enterPictureInPictureMode(context: android.content.Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .build()
        (context as? Activity)?.enterPictureInPictureMode(params)
    }
}

@Composable
fun OverlayControls(
    navController: NavHostController,
    onStopStreaming: () -> Unit,
    viewModel: StreamingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val streamState by viewModel.streamState.collectAsState()
    val currentConfig by viewModel.currentConfig.collectAsState()

    // Handle back press to enter PiP if camera overlay is enabled
    BackHandler(enabled = currentConfig.cameraOverlayEnabled) {
        enterPictureInPictureMode(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        // Chat overlay
        ChatOverlay()

        // Alert notifications
        AlertNotification()

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Control buttons row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Microphone toggle
                FloatingActionButton(
                    onClick = {
                        viewModel.toggleMicrophone(!currentConfig.microphoneEnabled)
                    },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = if (currentConfig.microphoneEnabled)
                        MaterialTheme.colors.primary
                    else
                        MaterialTheme.colors.surface
                ) {
                    Icon(
                        imageVector = if (currentConfig.microphoneEnabled)
                            Icons.Default.Mic
                        else
                            Icons.Default.MicOff,
                        contentDescription = "Toggle Microphone",
                        tint = if (currentConfig.microphoneEnabled)
                            Color.White
                        else
                            MaterialTheme.colors.onSurface
                    )
                }

                // Camera overlay toggle
                FloatingActionButton(
                    onClick = {
                        viewModel.toggleCameraOverlay(!currentConfig.cameraOverlayEnabled)
                    },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = if (currentConfig.cameraOverlayEnabled)
                        MaterialTheme.colors.primary
                    else
                        MaterialTheme.colors.surface
                ) {
                    Icon(
                        imageVector = if (currentConfig.cameraOverlayEnabled)
                            Icons.Default.Videocam
                        else
                            Icons.Default.VideocamOff,
                        contentDescription = "Toggle Camera Overlay",
                        tint = if (currentConfig.cameraOverlayEnabled)
                            Color.White
                        else
                            MaterialTheme.colors.onSurface
                    )
                }

                // Picture-in-Picture button (only show if camera overlay is enabled)
                if (currentConfig.cameraOverlayEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    FloatingActionButton(
                        onClick = { enterPictureInPictureMode(context) },
                        modifier = Modifier.size(48.dp),
                        backgroundColor = MaterialTheme.colors.secondary
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureInPicture,
                            contentDescription = "Enter Picture-in-Picture",
                            tint = Color.White
                        )
                    }
                }

                // Chat button
                FloatingActionButton(
                    onClick = { navController.navigate(FULL_CHAT) },
                    modifier = Modifier.size(48.dp),
                    backgroundColor = MaterialTheme.colors.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Open Full Chat",
                        tint = Color.White
                    )
                }

                // Stop streaming button
                FloatingActionButton(
                    onClick = onStopStreaming,
                    modifier = Modifier.size(56.dp),
                    backgroundColor = MaterialTheme.colors.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop Streaming",
                        tint = Color.White
                    )
                }
            }

            // Status indicator
            when (streamState) {
                is StreamState.Streaming -> {
                    val streamingState = streamState as StreamState.Streaming
                    val duration = (System.currentTimeMillis() - streamingState.startTime) / 1000
                    val minutes = duration / 60
                    val seconds = duration % 60

                    Text(
                        text = "LIVE - ${minutes}:${seconds.toString().padStart(2, '0')}",
                        color = Color.Red,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                is StreamState.Error -> {
                    Text(
                        text = "ERROR",
                        color = Color.White,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Red)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                else -> {
                    Text(
                        text = streamState::class.simpleName ?: "UNKNOWN",
                        color = Color.White,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}