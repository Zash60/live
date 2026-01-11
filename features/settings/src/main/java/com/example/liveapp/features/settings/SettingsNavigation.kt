package com.example.liveapp.features.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.liveapp.features.settings.ui.PrivacyPolicyScreen
import com.example.liveapp.features.settings.ui.SettingsScreen

const val SETTINGS_GRAPH = "settings_graph"
const val SETTINGS_SCREEN = "settings"
const val PRIVACY_POLICY_SCREEN = "privacy_policy"

fun NavGraphBuilder.settingsGraph(navController: NavHostController) {
    navigation(
        startDestination = SETTINGS_SCREEN,
        route = SETTINGS_GRAPH
    ) {
        composable(SETTINGS_SCREEN) {
            SettingsScreen(navController = navController)
        }
        composable(PRIVACY_POLICY_SCREEN) {
            PrivacyPolicyScreen(
                onConsentGiven = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
