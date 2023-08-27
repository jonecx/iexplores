package com.app.sambaaccesssmb.ui.feature

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.model.SmbSession
import com.app.sambaaccesssmb.ui.LoginViewModel
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Initial
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Loading
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.LoginInputValidationSuccessful
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Success
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.ValidatingLoginInput

@Composable
internal fun HomeRoute(
    onBackClick: () -> Unit,
    onNavigateToRemoteFile: () -> Unit,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val loginState = loginViewModel.loginUiState.collectAsStateWithLifecycle()

    when (loginState.value) {
        ValidatingLoginInput,
        LoginInputValidationSuccessful,
        Loading,
        -> LoginInProgress()
        Initial -> EnterCredentialScreen(loginViewModel::doLogin)
        is Error -> EnterCredentialScreen(loginViewModel::doLogin, loginState.value)
        is Success -> LaunchedEffect(loginState.value) {
            setSmbSession((loginState.value as Success).smbSession)
            onNavigateToRemoteFile.invoke()
        }
    }

    BackHandler(true) {
        onBackClick.invoke()
    }
}

private fun setSmbSession(smbSession: SmbSession) {
    SMBAccess.setSmbSession(smbSession)
}
