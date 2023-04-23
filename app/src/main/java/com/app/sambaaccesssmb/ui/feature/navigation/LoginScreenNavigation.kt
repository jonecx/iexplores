package com.app.sambaaccesssmb.ui.feature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.app.sambaaccesssmb.ui.LoginRoute

const val loginScreenRoute = "loginScreenRoute"

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) {
    this.navigate(loginScreenRoute, navOptions)
}

fun NavGraphBuilder.loginScreen(onBack: () -> Unit) {
    composable(route = loginScreenRoute) {
        LoginRoute(onBack)
    }
}
