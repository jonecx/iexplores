package com.app.sambaaccesssmb.ui.feature.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.feature.RemoteFileRoute
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel

const val remoteFileScreenRoute = "remoteFileScreenRoute/{shareName}"

fun NavController.navigateToRemoteFileScreen(shareName: String) {
    val encodedShareName = Uri.encode(shareName)
    this.navigate(
        "remoteFileScreenRoute/$encodedShareName",
        NavOptions.Builder()
            .build(),
    )
}

fun NavGraphBuilder.remoteFileScreen(
    onNavigateToHomeScreen: () -> Unit,
    onSmbFileClick: (String, String, Long) -> Unit,
    onDirectoryClick: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
    fileViewModel: FilesViewModel,
) {
    composable(route = remoteFileScreenRoute) {
        val shareName = it.arguments?.getString("shareName").orEmpty()
        RemoteFileRoute(onNavigateToHomeScreen, onSmbFileClick, onDirectoryClick, shareName, fileViewModel)
    }
    nestedGraphs()
}
