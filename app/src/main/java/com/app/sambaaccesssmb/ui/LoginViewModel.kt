package com.app.sambaaccesssmb.ui

import androidx.lifecycle.ViewModel
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import java.util.Properties
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val enableSMB2Property = "jcifs.smb.client.enableSMB2"
    private val distributedFileSystemProperty = "jcifs.smb.client.dfs.disabled"

    private lateinit var rootSMBFile: SmbFile

    fun login(serverAddress: String, username: String, password: String) {
        runCatching {
            val jcifsProperties = Properties().apply {
                setProperty(enableSMB2Property, true.toString())
                setProperty(distributedFileSystemProperty, false.toString())
            }

            val config = PropertyConfiguration(jcifsProperties)
            val baseContext = BaseContext(config)
            val ntlmPasswordAuthenticator = NtlmPasswordAuthenticator(serverAddress, username, password)
            val cifsContext: CIFSContext = baseContext.withCredentials(ntlmPasswordAuthenticator)
            rootSMBFile = SmbFile(serverAddress, cifsContext)
            rootSMBFile.connect()
        }.onSuccess {
            Success(rootSMBFile)
        }.onFailure {
            Error(it)
        }
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val smbFile: SmbFile) : LoginState()
        data class Error(val exception: Throwable) : LoginState()
    }
}
