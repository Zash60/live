package com.example.liveapp.features.streaming.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liveapp.features.streaming.StreamingViewModel

@Composable
fun NetworkIndicator(
    modifier: Modifier = Modifier,
    viewModel: StreamingViewModel = hiltViewModel()
) {
    val networkStats by viewModel.networkStats.collectAsState()

    val connectionQuality = when {
        networkStats.ping < 50 -> "Excellent"
        networkStats.ping < 100 -> "Good"
        networkStats.ping < 200 -> "Fair"
        else -> "Poor"
    }

    val qualityColor = when (connectionQuality) {
        "Excellent" -> Color.Green
        "Good" -> Color.Yellow
        "Fair" -> Color.Orange
        else -> Color.Red
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (networkStats.ping < 200) Icons.Default.Wifi else Icons.Default.WifiOff,
            contentDescription = "Network Status",
            tint = qualityColor,
            modifier = Modifier.size(16.dp)
        )

        Column {
            Text(
                text = "${networkStats.ping}ms",
                color = Color.White,
                style = MaterialTheme.typography.caption
            )
            Text(
                text = "${networkStats.currentBitrate}kbps",
                color = Color.White,
                style = MaterialTheme.typography.caption
            )
        }

        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(qualityColor)
        )
    }
}