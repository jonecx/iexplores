package com.app.sambaaccesssmb.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Initial
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import jcifs.smb.SmbFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginUsecase: LoginUsecase) : ViewModel() {

    private val _loginUser = MutableStateFlow<LoginState>(Initial)

    val loginUiState: StateFlow<LoginState> = _loginUser

    fun doLogin(serverAddress: String, username: String, password: String) {
        viewModelScope.launch {
            loginUsecase.login(serverAddress, username, password)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    initialValue = Loading
                )
                .collect {
                    _loginUser.value = it
                }
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        object Initial : LoginState()
        data class Success(val smbFile: SmbFile) : LoginState()
        data class Error(val exception: Throwable) : LoginState()
    }
}
