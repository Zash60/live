package com.example.liveapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.streaming.StreamingViewModel
import com.example.liveapp.features.streaming.domain.model.StreamState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onStartStreaming: () -> Unit,
    onNavigateToStreaming: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val streamingViewModel: StreamingViewModel = hiltViewModel()
    val streamState by streamingViewModel.streamState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Streaming Dashboard") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Channel Overview Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Channel Overview",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Status: ${if (streamState is StreamState.Streaming) "Live" else "Offline"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Followers: 1,234",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total Streams: 42",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                // Quick Start Streaming Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onStartStreaming
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Streaming"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Quick Start Streaming",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Begin your live stream now",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Recent Streams",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(listOf("Stream 1", "Stream 2", "Stream 3")) { stream ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToStreaming
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stream,
                            contentDescription = "Stream"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = stream,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "Duration: 2h 30m • Viewers: 150",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToStatistics,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Statistics")
                    }
                    OutlinedButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Settings")
                    }
                }
            }
        }
    }
}
