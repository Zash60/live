package com.example.liveapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.liveapp.features.auth.AUTH_GRAPH
import com.example.liveapp.features.auth.authNavGraph
import com.example.liveapp.features.chat.chatGraph
import com.example.liveapp.features.chat.CHAT_GRAPH
import com.example.liveapp.features.settings.settingsGraph
import com.example.liveapp.features.settings.SETTINGS_GRAPH
import com.example.liveapp.features.streaming.STREAMING_GRAPH
import com.example.liveapp.features.streaming.streamingGraph
import com.example.liveapp.ui.screens.DashboardScreen
import com.example.liveapp.ui.screens.StatisticsScreen

enum class MainScreen(val route: String, val title: String) {
    Dashboard("dashboard", "Dashboard"),
    Statistics("statistics", "Statistics"),
    Settings("settings", "Settings")
}

fun MainScreen.getIcon() = when (this) {
    MainScreen.Dashboard -> Icons.Filled.Dashboard
    MainScreen.Statistics -> Icons.Filled.BarChart
    MainScreen.Settings -> Icons.Filled.Settings
}

@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = AUTH_GRAPH
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Check if we're in the main app (not auth)
    val isInMainApp = currentRoute?.startsWith("main") == true || currentRoute in MainScreen.values().map { it.route }

    if (isInMainApp) {
        Scaffold(
            bottomBar = {
                MainBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigateToScreen = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = MainScreen.Dashboard.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(MainScreen.Dashboard.route) {
                    DashboardScreen(
                        onStartStreaming = {
                            navController.navigate(STREAMING_GRAPH)
                        },
                        onNavigateToStreaming = {
                            navController.navigate(STREAMING_GRAPH)
                        },
                        onNavigateToStatistics = {
                            navController.navigate(MainScreen.Statistics.route)
                        },
                        onNavigateToSettings = {
                            navController.navigate(MainScreen.Settings.route)
                        }
                    )
                }

                composable(MainScreen.Statistics.route) {
                    StatisticsScreen()
                }

                composable(MainScreen.Settings.route) {
                    com.example.liveapp.ui.screens.SettingsScreen()
                }

                // Include all feature graphs
                authNavGraph(navController) {
                    // After successful auth, navigate to main app
                    navController.navigate(MainScreen.Dashboard.route) {
                        popUpTo(AUTH_GRAPH) { inclusive = true }
                    }
                }

                streamingGraph(navController)
                chatGraph(navController)
                settingsGraph(navController)
            }
        }
    } else {
        // Auth flow without bottom navigation
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            authNavGraph(navController) {
                // After successful auth, navigate to main app
                navController.navigate(MainScreen.Dashboard.route) {
                    popUpTo(AUTH_GRAPH) { inclusive = true }
                }
            }

            streamingGraph(navController)
            chatGraph(navController)
            settingsGraph(navController)
        }
    }
}

@Composable
private fun MainBottomNavigation(
    currentRoute: String?,
    onNavigateToScreen: (MainScreen) -> Unit
) {
    NavigationBar {
        MainScreen.values().forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.getIcon(),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = { onNavigateToScreen(screen) }
            )
        }
    }
}