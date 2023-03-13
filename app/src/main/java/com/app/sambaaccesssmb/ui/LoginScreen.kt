package com.app.sambaaccesssmb.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Initial
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Loading
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Success
import com.app.sambaaccesssmb.ui.design.SmbTheme
import com.app.sambaaccesssmb.ui.design.ThemePreviews

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel()) {
    val loginState = viewModel.loginUiState.collectAsStateWithLifecycle()
    when (loginState.value) {
        Initial -> EnterCredentialScreen(viewModel::doLogin)
        Loading -> LoginInProgress()
        is Success -> Column { Text(text = stringResource(id = R.string.login_success)) }
        is Error -> EnterCredentialScreen(viewModel::doLogin, loginState.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterCredentialScreen(login: (String, String, String) -> Unit, error: LoginState? = null) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val smbAddress = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
        val username = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
        val password = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
        val keepSignIn = rememberSaveable { mutableStateOf(false) }

        error?.let {
            Text(text = stringResource(id = R.string.login_failure), color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            label = { Text(stringResource(id = R.string.server_address)) },
            value = smbAddress.value,
            onValueChange = { smbAddress.value = it },
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            label = { Text(stringResource(id = R.string.username)) },
            value = username.value,
            onValueChange = { username.value = it },
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            label = { Text(stringResource(id = R.string.password)) },
            value = password.value,
            onValueChange = { password.value = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = keepSignIn.value,
                onCheckedChange = { keepSignIn.value = it }
            )
            Spacer(Modifier.size(6.dp))
            Text(text = stringResource(id = R.string.keep_me_signed_in),)
            Spacer(modifier = Modifier.padding(end = 115.dp))
        }
        Button(onClick = {
            login(smbAddress.value.text, username.value.text, password.value.text)
        }) {
            Text(text = stringResource(id = R.string.sign_in))
        }
    }
}

@Composable
fun LoginInProgress() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IndeterminateProgressWheel(contentDesc = stringResource(id = R.string.please_wait_signing_in))
        Text(stringResource(id = R.string.signing_in_in_progress))
    }
}

@ThemePreviews
@Preview("Login screen")
@Composable
fun LoginScreenPreview() {
    SmbTheme {
        LoginScreen()
    }
}

@ThemePreviews
@Preview("Login in progress screen")
@Composable
fun LoginInProgressPreview() {
    SmbTheme {
        LoginInProgress()
    }
}
