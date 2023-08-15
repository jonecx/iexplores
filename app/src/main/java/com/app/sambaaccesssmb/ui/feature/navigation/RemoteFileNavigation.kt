package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.feature.RemoteFileRoute
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel

const val remoteFileScreenRoute = "remoteFileScreenRoute/{shareName}"

fun NavController.navigateToRemoteFileScreen(shareName: String) {
    this.navigate(
        "remoteFileScreenRoute/$shareName",
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
        val shareName = it.arguments?.getString("shareName").orEmpty()
        RemoteFileRoute(onNavigateToHomeScreen, onMediaClick, shareName, fileViewModel)
    }
    nestedGraphs()
}
