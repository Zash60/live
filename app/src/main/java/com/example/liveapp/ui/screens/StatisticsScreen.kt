package com.example.liveapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.streaming.domain.model.ScheduledStream
import com.example.liveapp.domain.model.StreamConfig
import com.example.liveapp.features.streaming.domain.model.StreamHistory
import com.example.liveapp.features.streaming.domain.usecase.SaveScheduledStreamUseCase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val streamHistory by viewModel.streamHistory.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val scheduledStreams by viewModel.scheduledStreams.collectAsState()
    var showScheduleDialog by remember { mutableStateOf(false) }
    var scheduleTitle by remember { mutableStateOf("") }
    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") }
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
                // Live History Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Live History"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live History",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Total Live Time: ${formatDuration(statistics?.averageDurationMinutes?.times(statistics?.totalStreams ?: 0) ?: 0.0)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Average Stream Duration: ${formatDuration(statistics?.averageDurationMinutes ?: 0.0)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total Streams: ${statistics?.totalStreams ?: 0}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Peak Viewers: ${statistics?.peakViewers ?: 0}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                // Viewer Stats Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Viewer Stats"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Viewer Statistics",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Peak Viewers: ${statistics?.peakViewers ?: 0}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Average Viewers: ${statistics?.averageViewers?.toInt() ?: 0}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total Streams: ${statistics?.totalStreams ?: 0}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Engagement: ${streamHistory.sumOf { it.engagementMetrics.likes + it.engagementMetrics.comments + it.engagementMetrics.shares + it.engagementMetrics.follows }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            
                if (showScheduleDialog) {
                    AlertDialog(
                        onDismissRequest = { showScheduleDialog = false },
                        title = { Text("Schedule Stream") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = scheduleTitle,
                                    onValueChange = { scheduleTitle = it },
                                    label = { Text("Title") }
                                )
                                // For simplicity, use text for date/time
                                Text("Scheduled for: ${selectedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")
                                // In real app, use DatePicker and TimePicker
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (scheduleTitle.isNotBlank()) {
                                    val config = StreamConfig() // Use default
                                    val scheduled = ScheduledStream(
                                        title = scheduleTitle,
                                        scheduledTime = selectedDateTime,
                                        config = config
                                    )
                                    viewModel.saveScheduledStream(scheduled)
                                    scheduleTitle = ""
                                    showScheduleDialog = false
                                }
                            }) {
                                Text("Schedule")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showScheduleDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
            
            private fun formatDuration(minutes: Double): String {
                val totalMinutes = minutes.toInt()
                val hours = totalMinutes / 60
                val mins = totalMinutes % 60
                return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
            }

            item {
                // Performance Metrics Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Performance Metrics"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Performance Metrics",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Average Bitrate: 3000 Kbps",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Stream Quality: 1080p 60fps",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Dropped Frames: 0.1%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Network Latency: 45ms",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                // Recent Streams List
                Text(
                    text = "Recent Streams",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(streamHistory.take(10)) { stream ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stream.title,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${formatDuration(stream.duration.toDouble())} • ${stream.peakViewers} viewers • ${stream.startTime.format(DateTimeFormatter.ofPattern("MMM dd"))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scheduled Streams",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = { showScheduleDialog = true }) {
                        Text("Schedule")
                    }
                }
            }

            items(scheduledStreams) { scheduled ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = scheduled.title,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = scheduled.scheduledTime.format(DateTimeFormatter.ofPattern("MMM dd HH:mm")),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}