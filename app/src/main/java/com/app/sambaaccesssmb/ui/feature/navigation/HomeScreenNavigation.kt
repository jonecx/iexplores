package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.design.HomeRoute

const val homeScreenRoute = "homeScreenRoute"

fun NavController.navigateToHomeScreen(
    navOptions: NavOptions? = null
) {
    this.navigate(homeScreenRoute, navOptions)
}

fun NavGraphBuilder.homeScreen(
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToRemoteFile: () -> Unit,
) {
    composable(route = homeScreenRoute) {
        HomeRoute(onNavigateToLoginScreen, onNavigateToRemoteFile)
    }
}
