package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.feature.HomeRoute

const val homeScreenRoute = "homeScreenRoute"

fun NavController.navigateToHomeScreen() {
    this.navigate(
        homeScreenRoute,
        NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(homeScreenRoute, inclusive = true)
            .build(),
    )
}

fun NavGraphBuilder.homeScreen(
    onBackClick: () -> Unit,
    onNavigateToRemoteFile: () -> Unit,
) {
    composable(route = homeScreenRoute) {
        HomeRoute(onBackClick, onNavigateToRemoteFile)
    }
}
