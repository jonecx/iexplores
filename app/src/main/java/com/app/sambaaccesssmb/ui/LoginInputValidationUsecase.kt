package com.app.sambaaccesssmb.ui

import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.PasswordKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.ServerAddressKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.UsernameKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.LoginInputValidationSuccessful
import com.app.sambaaccesssmb.utils.isValidAddress
import com.app.sambaaccesssmb.utils.isValidPassword
import com.app.sambaaccesssmb.utils.isValidUsername
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginInputValidationUsecase @Inject constructor() {
    operator fun invoke(
        serverAddress: String,
        username: String,
        password: String
    ): Flow<LoginState> = flow {
        if (!serverAddress.isValidAddress()) {
            emit(Error(validationKey = ServerAddressKey))
            return@flow
        }

        if (!username.isValidUsername()) {
            emit(Error(validationKey = UsernameKey))
            return@flow
        }

        if (!password.isValidPassword()) {
            emit(Error(validationKey = PasswordKey))
            return@flow
        }
        emit(LoginInputValidationSuccessful)
    }
}
