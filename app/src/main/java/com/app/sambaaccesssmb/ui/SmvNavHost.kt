package com.app.sambaaccesssmb.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.app.sambaaccesssmb.ui.feature.navigation.homeScreen
import com.app.sambaaccesssmb.ui.feature.navigation.homeScreenRoute
import com.app.sambaaccesssmb.ui.feature.navigation.loginScreen
import com.app.sambaaccesssmb.ui.feature.navigation.loginScreenRoute
import com.app.sambaaccesssmb.ui.feature.navigation.navigateToRemoteFileScreen
import com.app.sambaaccesssmb.ui.feature.navigation.remoteFileScreen
import com.app.sambaaccesssmb.ui.feature.navigation.remoteFileScreenRoute

@Composable
fun SmbNavHost(
    navController: NavHostController,
    startDestination: String = homeScreenRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        loginScreen(onBack = navController::popBackStack) // temporary setup
        homeScreen(
            onNavigateToLoginScreen = { navController.navigate(loginScreenRoute) },
            onNavigateToRemoteFile = { navController.navigate(remoteFileScreenRoute) }
        )
        remoteFileScreen(navController::navigateToRemoteFileScreen)
    }
}
