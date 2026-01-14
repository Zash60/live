package com.example.liveapp.features.streaming.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.streaming.StreamingViewModel
import com.example.liveapp.features.streaming.domain.model.QualityPreset
import com.example.liveapp.domain.model.Resolution
import com.example.liveapp.domain.model.YouTubePrivacyStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamingConfigScreen(
    onStartStreaming: () -> Unit,
    viewModel: StreamingViewModel = hiltViewModel()
) {
    val currentConfig by viewModel.currentConfig.collectAsState()
    val qualityPresets by viewModel.qualityPresets.collectAsState()

    var streamUrl by remember { mutableStateOf(currentConfig.streamUrl) }
    var streamKey by remember { mutableStateOf(currentConfig.streamKey) }
    var selectedPreset by remember { mutableStateOf<QualityPreset?>(null) }

    // YouTube Live settings
    var useYouTubeLive by remember { mutableStateOf(currentConfig.useYouTubeLive) }
    var youTubeTitle by remember { mutableStateOf(currentConfig.youTubeEventTitle) }
    var youTubeDescription by remember { mutableStateOf(currentConfig.youTubeEventDescription) }
    var youTubePrivacy by remember { mutableStateOf(currentConfig.youTubePrivacyStatus) }
    var isCreatingEvent by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Streaming Configuration",
                style = MaterialTheme.typography.h5
            )
        }

        item {
            OutlinedTextField(
                value = streamUrl,
                onValueChange = {
                    streamUrl = it
                    viewModel.updateStreamUrl(it)
                },
                label = { Text("Stream URL") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
        }

        item {
            OutlinedTextField(
                value = streamKey,
                onValueChange = {
                    streamKey = it
                    viewModel.updateStreamKey(it)
                },
                label = { Text("Stream Key") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !useYouTubeLive
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Use YouTube Live")
                Switch(
                    checked = useYouTubeLive,
                    onCheckedChange = {
                        useYouTubeLive = it
                        viewModel.updateUseYouTubeLive(it)
                    }
                )
            }
        }

        if (useYouTubeLive) {
            item {
                Text(
                    text = "YouTube Live Event",
                    style = MaterialTheme.typography.h6
                )
            }

            item {
                OutlinedTextField(
                    value = youTubeTitle,
                    onValueChange = {
                        youTubeTitle = it
                        viewModel.updateYouTubeTitle(it)
                    },
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("My Gaming Stream") }
                )
            }

            item {
                OutlinedTextField(
                    value = youTubeDescription,
                    onValueChange = {
                        youTubeDescription = it
                        viewModel.updateYouTubeDescription(it)
                    },
                    label = { Text("Event Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    placeholder = { Text("Join me for an exciting gaming session! #gaming #gameplay") }
                )
            }

            item {
                Text(
                    text = "Privacy Setting",
                    style = MaterialTheme.typography.body1
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    YouTubePrivacyStatus.values().forEach { status ->
                        FilterChip(
                            selected = youTubePrivacy == status,
                            onClick = {
                                youTubePrivacy = status
                                viewModel.updateYouTubePrivacy(status)
                            },
                            label = { Text(status.name.lowercase().capitalize()) }
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        isCreatingEvent = true
                        viewModel.createYouTubeEvent()
                        isCreatingEvent = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = youTubeTitle.isNotBlank() && !isCreatingEvent
                ) {
                    if (isCreatingEvent) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Creating Event...")
                    } else {
                        Icon(Icons.Default.Videocam, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create YouTube Live Event")
                    }
                }
            }
        }

        item {
            Text(
                text = "Quality Presets",
                style = MaterialTheme.typography.h6
            )
        }

        items(qualityPresets.size) { index ->
            val preset = qualityPresets[index]
            QualityPresetCard(
                preset = preset,
                isSelected = selectedPreset == preset,
                onSelect = {
                    selectedPreset = preset
                    viewModel.selectQualityPreset(preset)
                }
            )
        }

        item {
            Text(
                text = "Audio Settings",
                style = MaterialTheme.typography.h6
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Audio")
                Switch(
                    checked = currentConfig.audioEnabled,
                    onCheckedChange = { viewModel.toggleAudio(it) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Microphone")
                Switch(
                    checked = currentConfig.microphoneEnabled,
                    onCheckedChange = { viewModel.toggleMicrophone(it) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Camera Overlay")
                Switch(
                    checked = currentConfig.cameraOverlayEnabled,
                    onCheckedChange = { viewModel.toggleCameraOverlay(it) }
                )
            }
        }

        item {
            Button(
                onClick = onStartStreaming,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = streamUrl.isNotBlank() && streamKey.isNotBlank()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Streaming")
            }
        }
    }
}

@Composable
private fun QualityPresetCard(
    preset: QualityPreset,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = if (isSelected) 8.dp else 2.dp,
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = preset.name,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "${preset.resolution.width}x${preset.resolution.height} @ ${preset.fps}fps",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "${preset.bitrate} kbps",
                    style = MaterialTheme.typography.body2
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
        }
    }
}
