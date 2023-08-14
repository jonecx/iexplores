package com.app.sambaaccesssmb.ui

import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Success
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Success2
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.share.DiskShare
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Properties
import javax.inject.Inject

class LoginUsecase @Inject constructor() {
    private val enableSMB2Property = "jcifs.smb.client.enableSMB2"
    private val distributedFileSystemProperty = "jcifs.smb.client.dfs.disabled"

    private lateinit var rootSMBFile: SmbFile

    operator fun invoke(
        _serverAddress: String,
        _username: String,
        _password: String,
    ): Flow<LoginState> = flow {
        val smbServerAddress = "smb://$_serverAddress"
        runCatching {
            val jcifsProperties = Properties().apply {
                setProperty(enableSMB2Property, true.toString())
                setProperty(distributedFileSystemProperty, false.toString())
            }

            val config = PropertyConfiguration(jcifsProperties)
            val baseContext = BaseContext(config)
            val ntlmPasswordAuthenticator =
                NtlmPasswordAuthenticator(smbServerAddress, _username, _password)
            val cifsContext: CIFSContext =
                baseContext.withCredentials(ntlmPasswordAuthenticator)
            rootSMBFile = SmbFile(smbServerAddress, cifsContext)
            rootSMBFile.connect()
        }.onSuccess {
            emit(Success(rootSMBFile))
        }.onFailure {
            emit(Error(it))
        }
    }.flowOn(Dispatchers.IO)

    operator fun invoke(
        _serverAddress: String,
        _username: String,
        _password: String,
        _shareName: String,
    ): Flow<LoginState> = flow<LoginState> {
        lateinit var diskShare: DiskShare
        runCatching {
            val connection = SMBAccess.getSmbClientInstance().connect(_serverAddress)
            val authenticationContext = AuthenticationContext(_username, _password.toCharArray(), "")
            val session = connection.authenticate(authenticationContext)
            diskShare = session.connectShare(_shareName) as DiskShare
        }.onSuccess {
            emit(Success2(diskShare))
        }.onFailure {
            emit(Error(it))
        }
    }.flowOn(Dispatchers.IO)
}
