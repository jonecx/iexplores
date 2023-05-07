package com.app.sambaaccesssmb.ui.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.app.sambaaccesssmb.BuildConfig
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.R.string
import com.app.sambaaccesssmb.ui.IndeterminateProgressWheel
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.GeneralErrorKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.PasswordKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.ServerAddressKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.UsernameKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.design.SmbTheme
import com.app.sambaaccesssmb.ui.design.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterCredentialScreen(login: (String, String, String) -> Unit, loginState: LoginState? = null) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val smbAddress = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text = if (BuildConfig.DEBUG) BuildConfig.SERVER_ADDRESS else "")) }
        val username = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text = if (BuildConfig.DEBUG) BuildConfig.USERNAME else "")) }
        val password = rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text = if (BuildConfig.DEBUG) BuildConfig.PASSWORD else "")) }
        val keepSignIn = rememberSaveable { mutableStateOf(false) }

        if (isError(loginState)) {
            Text(text = stringResource(id = R.string.login_failure), color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            label = { Text(stringResource(id = R.string.server_address)) },
            value = smbAddress.value,
            onValueChange = { smbAddress.value = it },
            singleLine = true,
            isError = isError(loginState, ServerAddressKey),
            supportingText = { SupportingText(loginState = loginState, ServerAddressKey) },
            trailingIcon = { TrailingIcon(loginState = loginState, ServerAddressKey) }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            label = { Text(stringResource(id = R.string.username)) },
            value = username.value,
            onValueChange = { username.value = it },
            singleLine = true,
            isError = isError(loginState, UsernameKey),
            supportingText = { SupportingText(loginState = loginState, UsernameKey) },
            trailingIcon = { TrailingIcon(loginState = loginState, UsernameKey) }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            label = { Text(stringResource(id = R.string.password)) },
            value = password.value,
            onValueChange = { password.value = it },
            singleLine = true,
            isError = isError(loginState, PasswordKey),
            supportingText = { SupportingText(loginState = loginState, PasswordKey) },
            trailingIcon = { TrailingIcon(loginState = loginState, PasswordKey) },
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
private fun getErrorString(validationKey: LoginInputValidationKey): String {
    return stringResource(
        id = when (validationKey) {
            ServerAddressKey -> R.string.invalid_server_address_error
            UsernameKey -> R.string.invalid_username_error
            PasswordKey -> R.string.invalid_password_error
            GeneralErrorKey -> R.string.unknown_error
        }
    )
}

@Composable
private fun SupportingText(loginState: LoginState?, validationKey: LoginInputValidationKey = GeneralErrorKey) {
    if (isError(loginState, validationKey)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = getErrorString(validationKey = validationKey),
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun TrailingIcon(loginState: LoginState?, validationKey: LoginInputValidationKey = GeneralErrorKey) {
    if (isError(loginState, validationKey)) {
        Icon(
            Icons.Filled.Error,
            getErrorString(validationKey = validationKey),
            tint = MaterialTheme.colorScheme.error
        )
    }
}

private fun isError(loginState: LoginState?, validationKey: LoginInputValidationKey = GeneralErrorKey): Boolean {
    return loginState?.let {
        loginState is Error && loginState.validationKey == validationKey
    } ?: false
}

@Composable
fun LoginInProgress() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IndeterminateProgressWheel(contentDesc = stringResource(id = string.please_wait_signing_in))
        Text(stringResource(id = R.string.signing_in_in_progress))
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
