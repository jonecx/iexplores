package com.app.sambaaccesssmb.ui.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sambaaccesssmb.ui.feature.EnterCredentialScreen
import com.app.sambaaccesssmb.ui.feature.LoginInProgress
import com.app.sambaaccesssmb.ui.LoginViewModel
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Initial
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Loading
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.LoginInputValidationSuccessful
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Success
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.ValidatingLoginInput

@Composable
internal fun HomeRoute(
    onNavigateToLoginScreen: () -> Unit,
    onNavigateToRemoteFile: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val loginState = loginViewModel.loginUiState.collectAsStateWithLifecycle()

    when (loginState.value) {
        ValidatingLoginInput,
        LoginInputValidationSuccessful,
        Loading -> LoginInProgress()
        Initial -> EnterCredentialScreen(loginViewModel::doLogin)
        is Error -> EnterCredentialScreen(loginViewModel::doLogin, loginState.value)
        is Success -> LaunchedEffect(loginState) {
            onNavigateToRemoteFile.invoke()
        }
    }
}
