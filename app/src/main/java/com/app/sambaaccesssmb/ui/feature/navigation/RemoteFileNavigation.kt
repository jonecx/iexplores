package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.feature.RemoteFileRoute
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel

const val remoteFileScreenRoute = "remoteFileScreenRoute"

fun NavController.navigateToRemoteFileScreen() {
    this.navigate(
        remoteFileScreenRoute,
        NavOptions.Builder()
            .setPopUpTo(remoteFileScreenRoute, inclusive = true)
            .build(),
    )
}

fun NavGraphBuilder.remoteFileScreen(
    onNavigateToHomeScreen: () -> Unit,
    onMediaClick: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    fileViewModel: FilesViewModel,
) {
    composable(route = remoteFileScreenRoute) {
        RemoteFileRoute(onNavigateToHomeScreen, onMediaClick, fileViewModel)
    }
    nestedGraphs()
}
