package com.app.sambaaccesssmb.ui

import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.model.SmbSession
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Error
import com.app.sambaaccesssmb.ui.LoginViewModel.LoginState.Success
import com.hierynomus.smbj.auth.AuthenticationContext
import com.rapid7.client.dcerpc.mssrvs.ServerService
import com.rapid7.client.dcerpc.transport.SMBTransportFactories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginUsecase @Inject constructor() {
    /*    private val enableSMB2Property = "jcifs.smb.client.enableSMB2"
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
        }.flowOn(Dispatchers.IO)*/

    operator fun invoke(
        _serverAddress: String,
        _username: String,
        _password: String,
    ): Flow<LoginState> = flow<LoginState> {
        lateinit var smbSession: SmbSession
        runCatching {
            val connection = SMBAccess.getSmbClientInstance().connect(_serverAddress)
            val authenticationContext = AuthenticationContext(_username, _password.toCharArray(), "")
            val session = connection.authenticate(authenticationContext)
            val rpcTransport = SMBTransportFactories.SRVSVC.getTransport(session)
            val serverService = ServerService(rpcTransport)
            smbSession = SmbSession(serverService, session)
        }.onSuccess {
            emit(Success(smbSession))
        }.onFailure {
            emit(Error(it))
        }
    }.flowOn(Dispatchers.IO)
}
