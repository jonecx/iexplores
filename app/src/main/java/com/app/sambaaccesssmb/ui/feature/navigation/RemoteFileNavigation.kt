package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.feature.RemoteFileRoute

const val remoteFileScreenRoute = "remoteFileScreenRoute"

fun NavController.navigateToRemoteFileScreen() {
    this.navigate(
        remoteFileScreenRoute,
        NavOptions.Builder()
            .setPopUpTo(remoteFileScreenRoute, inclusive = true)
            .build()
    )
}

fun NavGraphBuilder.remoteFileScreen(
    onNavigateToHomeScreen: () -> Unit
) {
    composable(route = remoteFileScreenRoute) {
        RemoteFileRoute(onNavigateToHomeScreen)
    }
}
