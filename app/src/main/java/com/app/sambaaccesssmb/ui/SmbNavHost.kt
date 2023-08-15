package com.app.sambaaccesssmb.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel
import com.app.sambaaccesssmb.ui.feature.navigation.homeScreen
import com.app.sambaaccesssmb.ui.feature.navigation.mediaScreen
import com.app.sambaaccesssmb.ui.feature.navigation.navigateToHomeScreen
import com.app.sambaaccesssmb.ui.feature.navigation.navigateToMedia
import com.app.sambaaccesssmb.ui.feature.navigation.navigateToNetShareScreen
import com.app.sambaaccesssmb.ui.feature.navigation.navigateToRemoteFileScreen
import com.app.sambaaccesssmb.ui.feature.navigation.netShareScreen
import com.app.sambaaccesssmb.ui.feature.navigation.netShareScreenRoute
import com.app.sambaaccesssmb.ui.feature.navigation.remoteFileScreen

@Composable
fun SmbNavHost(
    navController: NavHostController,
    startDestination: String = netShareScreenRoute,
) {
    val context = LocalContext.current
    val fileViewModel: FilesViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        homeScreen(
            onBackClick = { (context as ComponentActivity).finish() },
            onNavigateToShareScreen = { navController.navigateToNetShareScreen() },
        )
        netShareScreen(
            onNavigateToHomeScreen = { navController.navigateToHomeScreen() },
            onShareClick = { shareName ->
                navController.navigateToRemoteFileScreen(shareName)
            },
            fileViewModel,
        )
        remoteFileScreen(
            onNavigateToHomeScreen = { navController.navigateToHomeScreen() },
            onMediaClick = { smbFile ->
//                fileViewModel.selectedSmbFile = smbFile
                navController.navigateToMedia(smbFile)
            },
            nestedGraphs = {
                mediaScreen(
                    onBackClick = navController::popBackStack,
                    onMediaClick = {},
                    fileViewModel,
                )
            },
            fileViewModel,
        )
    }
}
