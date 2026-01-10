package com.example.liveapp.features.streaming

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.liveapp.features.chat.ui.FullChatScreen
import com.example.liveapp.features.streaming.ui.OverlayControls
import com.example.liveapp.features.streaming.ui.PreviewScreen
import com.example.liveapp.features.streaming.ui.StreamingConfigScreen

const val STREAMING_GRAPH = "streaming_graph"
const val STREAMING_CONFIG = "streaming_config"
const val STREAMING_PREVIEW = "streaming_preview"
const val STREAMING_OVERLAY = "streaming_overlay"
const val FULL_CHAT = "full_chat"

fun NavGraphBuilder.streamingGraph(navController: NavHostController) {
    navigation(
        startDestination = STREAMING_CONFIG,
        route = STREAMING_GRAPH
    ) {
        composable(STREAMING_CONFIG) {
            StreamingConfigScreen(
                onStartStreaming = {
                    navController.navigate(STREAMING_PREVIEW)
                }
            )
        }

        composable(STREAMING_PREVIEW) {
            PreviewScreen(
                onBack = {
                    navController.popBackStack()
                },
                onStartStreaming = {
                    navController.navigate(STREAMING_OVERLAY)
                }
            )
        }

        composable(STREAMING_OVERLAY) {
            OverlayControls(
                navController = navController,
                onStopStreaming = {
                    navController.popBackStack(STREAMING_CONFIG, inclusive = false)
                }
            )
        }

        composable(FULL_CHAT) {
            FullChatScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}