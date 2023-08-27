package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.feature.NetShareRoute
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel

const val netShareScreenRoute = "netShareScreenRoute"

fun NavController.navigateToNetShareScreen() {
    this.navigate(
        netShareScreenRoute,
        NavOptions.Builder()
            .setPopUpTo(netShareScreenRoute, inclusive = true)
            .build(),
    )
}

fun NavGraphBuilder.netShareScreen(
    onNavigateToHomeScreen: () -> Unit,
    onShareClick: (String) -> Unit,
    fileViewModel: FilesViewModel,
) {
    composable(route = netShareScreenRoute) {
        NetShareRoute(onNavigateToHomeScreen, onShareClick, fileViewModel)
    }
}
