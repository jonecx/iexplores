package com.app.sambaaccesssmb.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.app.sambaaccesssmb.ui.feature.navigation.homeScreen
import com.app.sambaaccesssmb.ui.feature.navigation.navigateToHomeScreen
import com.app.sambaaccesssmb.ui.feature.navigation.navigateToRemoteFileScreen
import com.app.sambaaccesssmb.ui.feature.navigation.remoteFileScreen
import com.app.sambaaccesssmb.ui.feature.navigation.remoteFileScreenRoute

@Composable
fun SmbNavHost(
    navController: NavHostController,
    startDestination: String = remoteFileScreenRoute
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeScreen(
            onBackClick = { (context as ComponentActivity).finish() },
            onNavigateToRemoteFile = { navController.navigateToRemoteFileScreen() }
        )
        remoteFileScreen(
            onNavigateToHomeScreen = { navController.navigateToHomeScreen() }
        )
    }
}
