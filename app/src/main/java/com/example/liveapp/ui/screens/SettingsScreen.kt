package com.example.liveapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.liveapp.features.settings.PRIVACY_POLICY_SCREEN
import com.example.liveapp.features.settings.SettingsViewModel
import com.example.liveapp.features.streaming.domain.model.StreamConfig
import com.example.liveapp.features.streaming.domain.model.StreamPreset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val settings by viewModel.settings.collectAsState()
    val streamPresets by viewModel.streamPresets.collectAsState()
    var showSavePresetDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
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
                // App Preferences Section
                Text(
                    text = "App Preferences",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = "Theme"
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dark Mode",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Switch(
                                checked = settings.darkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                modifier = Modifier.testTag("dark_mode_switch")
                            )
                        }
                    }
                }
            }

            item {
                // Stream Presets Section
                Text(
                    text = "Stream Presets",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Stream Presets",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(onClick = { showSavePresetDialog = true }) {
                                Text("Save Current")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        streamPresets.forEach { preset ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(preset.name)
                                Row {
                                    Button(onClick = { /* Load preset */ }) {
                                        Text("Load")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { viewModel.deleteStreamPreset(preset.id) }) {
                                        Text("Delete")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            
                if (showSavePresetDialog) {
                    AlertDialog(
                        onDismissRequest = { showSavePresetDialog = false },
                        title = { Text("Save Preset") },
                        text = {
                            OutlinedTextField(
                                value = presetName,
                                onValueChange = { presetName = it },
                                label = { Text("Preset Name") }
                            )
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (presetName.isNotBlank()) {
                                    val config = StreamConfig() // Use current config, in real app get from streaming VM
                                    val preset = StreamPreset(name = presetName, config = config)
                                    viewModel.saveStreamPreset(preset)
                                    presetName = ""
                                    showSavePresetDialog = false
                                }
                            }) {
                                Text("Save")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showSavePresetDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }

            item {
                // Notifications Section
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications"
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Push Notifications",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Switch(
                                checked = settings.notificationsEnabled,
                                onCheckedChange = { viewModel.toggleNotifications() }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Chat Alerts",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = settings.chatAlerts,
                                onCheckedChange = { viewModel.toggleChatAlerts() }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Follower Alerts",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = settings.followerAlerts,
                                onCheckedChange = { viewModel.toggleFollowerAlerts() }
                            )
                        }
                    }
                }
            }

            item {
                // Privacy & Compliance Section
                Text(
                    text = "Privacy & Compliance",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Policy,
                                    contentDescription = "Privacy Policy"
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Privacy Policy",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            TextButton(onClick = { navController.navigate(PRIVACY_POLICY_SCREEN) }) {
                                Text("View")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Data Collection Consent",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = settings.dataCollectionConsent,
                                onCheckedChange = { viewModel.toggleDataCollectionConsent() },
                                modifier = Modifier.semantics { contentDescription = "Data Collection Consent Switch" }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Analytics Enabled",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = settings.analyticsEnabled,
                                onCheckedChange = { viewModel.toggleAnalytics() },
                                modifier = Modifier.semantics { contentDescription = "Analytics Enabled Switch" }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.requestDataDeletion() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "Request Data Deletion Button" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Request Data Deletion")
                        }
                    }
                }
            }

            item {
                // Accessibility Section
                Text(
                    text = "Accessibility",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "High Contrast Mode",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = settings.highContrastMode,
                                onCheckedChange = { viewModel.toggleHighContrastMode() },
                                modifier = Modifier.semantics { contentDescription = "High Contrast Mode Switch" }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Larger Touch Targets",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Switch(
                                checked = settings.largerTouchTargets,
                                onCheckedChange = { viewModel.toggleLargerTouchTargets() },
                                modifier = Modifier.semantics { contentDescription = "Larger Touch Targets Switch" }
                            )
                        }
                    }
                }
            }

            item {
                // Additional Settings
                Text(
                    text = "Advanced",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Bitrate: ${settings.bitrate} Kbps",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Frame Rate: ${settings.frameRate} fps",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Resolution: ${settings.resolution}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}