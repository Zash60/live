package com.example.liveapp.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val AUTH_GRAPH = "auth_graph"
const val LOGIN_SCREEN = "login"
const val PROFILE_SCREEN = "profile"

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onAuthSuccess: () -> Unit
) {
    navigation(
        startDestination = LOGIN_SCREEN,
        route = AUTH_GRAPH
    ) {
        composable(LOGIN_SCREEN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(PROFILE_SCREEN) {
                        popUpTo(LOGIN_SCREEN) { inclusive = true }
                    }
                }
            )
        }
        composable(PROFILE_SCREEN) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(LOGIN_SCREEN) {
                        popUpTo(PROFILE_SCREEN) { inclusive = true }
                    }
                }
            )
        }
    }
}