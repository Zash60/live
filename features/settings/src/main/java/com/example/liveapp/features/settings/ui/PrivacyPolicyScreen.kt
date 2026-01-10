package com.example.liveapp.features.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onConsentGiven: () -> Unit,
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Add back icon if needed
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
                .semantics { contentDescription = "Privacy Policy Screen" },
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = """
                    This privacy policy explains how we collect, use, and protect your personal information.

                    1. Information We Collect:
                    - Account information (username, email)
                    - Streaming data and preferences
                    - Chat messages and interactions
                    - Device information and usage analytics

                    2. How We Use Your Information:
                    - To provide streaming services
                    - To improve our app functionality
                    - To send notifications (with your consent)
                    - To comply with legal requirements

                    3. Data Sharing:
                    - We do not sell your personal information
                    - Data may be shared with service providers for app functionality
                    - Aggregated, anonymized data may be used for analytics

                    4. Your Rights:
                    - Access your personal data
                    - Correct inaccurate data
                    - Delete your data
                    - Withdraw consent for data processing

                    5. Data Security:
                    - We implement appropriate security measures
                    - Data is encrypted in transit and at rest
                    - Regular security audits are performed

                    For more information, contact our privacy team.
                """.trimIndent(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.semantics { contentDescription = "Privacy Policy Text" }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Decline Privacy Consent Button" }
                ) {
                    Text("Decline")
                }

                Button(
                    onClick = {
                        viewModel.givePrivacyConsent()
                        onConsentGiven()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Accept Privacy Consent Button" }
                ) {
                    Text("Accept")
                }
            }
        }
    }
}