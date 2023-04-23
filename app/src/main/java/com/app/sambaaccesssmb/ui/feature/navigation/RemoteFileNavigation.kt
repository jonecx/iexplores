package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.feature.RemoteFileRoute

const val remoteFileScreenRoute = "remoteFileScreenRoute"

fun NavController.navigateToRemoteFileScreen(
    navOptions: NavOptions? = null
) {
    this.navigate(remoteFileScreenRoute, navOptions)
}

fun NavGraphBuilder.remoteFileScreen(
    onNavigateToRemoteFile: () -> Unit
) {
    composable(route = remoteFileScreenRoute) {
        RemoteFileRoute(onNavigateToRemoteFile)
    }
}
