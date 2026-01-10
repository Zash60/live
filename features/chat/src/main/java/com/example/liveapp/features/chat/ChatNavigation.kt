package com.example.liveapp.features.chat

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.liveapp.features.chat.ui.FullChatScreen

const val CHAT_GRAPH = "chat_graph"
const val FULL_CHAT = "full_chat"

fun NavGraphBuilder.chatGraph(navController: NavHostController) {
    navigation(
        startDestination = FULL_CHAT,
        route = CHAT_GRAPH
    ) {
        composable(FULL_CHAT) {
            FullChatScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}