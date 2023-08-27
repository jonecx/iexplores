package com.app.sambaaccesssmb.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sambaaccesssmb.model.SmbSession
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginInputValidationKey.GeneralErrorKey
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Initial
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Loading
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.LoginInputValidationSuccessful
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.ValidatingLoginInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginInputValidationUsecase: LoginInputValidationUsecase, private val loginUsecase: LoginUsecase) : ViewModel() {

    private val _loginUser = MutableStateFlow<LoginState>(Initial)

    val loginUiState: StateFlow<LoginState> = _loginUser

    fun doLogin(serverAddress: String, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            validateLoginInput(serverAddress, username, password).collect { loginState ->
                when (loginState) {
                    is Error -> _loginUser.value = loginState
                    is LoginInputValidationSuccessful -> login(serverAddress, username, password).collect {
                        _loginUser.value = it
                    }
                    else -> _loginUser.value = loginState
                }
            }
        }
    }

    private fun login(serverAddress: String, username: String, password: String) = loginUsecase(serverAddress, username, password)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

    private fun validateLoginInput(serverAddress: String, username: String, password: String) = loginInputValidationUsecase(serverAddress, username, password)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            initialValue = ValidatingLoginInput,
        )

    sealed class LoginInputValidationKey {
        object GeneralErrorKey : LoginInputValidationKey()
        object ServerAddressKey : LoginInputValidationKey()
        object UsernameKey : LoginInputValidationKey()
        object PasswordKey : LoginInputValidationKey()
    }

    sealed class LoginState {
        object Loading : LoginState()
        object ValidatingLoginInput : LoginState()
        object Initial : LoginState()
        object LoginInputValidationSuccessful : LoginState()
        data class Success(val smbSession: SmbSession) : LoginState()
        data class Error(val exception: Throwable = Exception(""), val validationKey: LoginInputValidationKey = GeneralErrorKey) : LoginState()
    }
}
