package com.app.sambaaccesssmb.ui.feature.fvm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sambaaccesssmb.ui.FileCursorUseCase
import com.app.sambaaccesssmb.ui.FileDownloadUseCase
import com.app.sambaaccesssmb.ui.NetShareUseCase
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Loading
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo1
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor(private val fileDownloadUseCase: FileDownloadUseCase, private val fileCursorUseCase: FileCursorUseCase, private val netShareUseCase: NetShareUseCase) : ViewModel() {

    companion object {
        private const val TAG = "FilesViewModel"
    }

    private val _fileDownloadState = MutableStateFlow<FileState>(Loading)
    val fileDownloadState: StateFlow<FileState> = _fileDownloadState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Loading,
    )

    private val _fileCursorState = MutableStateFlow<FileState>(Loading)
    val fileCursorState: StateFlow<FileState> = _fileCursorState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Loading,
    )

    val netShareState: StateFlow<FileState> = netShareUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Loading,
    )

    fun getFileCursor(shareName: String) {
        viewModelScope.launch {
            fileCursorUseCase(shareName).collectLatest {
                _fileCursorState.value = it
            }
        }
    }

    fun getSmbFile(shareName: String, smbFilePath: String, smbFileSize: Long) {
        viewModelScope.launch {
            fileDownloadUseCase(shareName, smbFilePath, smbFileSize).collectLatest {
                _fileDownloadState.value = it
            }
        }
    }
}

sealed interface FileState {
    data class CursorState(val smbItems: List<FileIdBothDirectoryInformation>) : FileState
    data class NetShareInfoState(val netShares: List<NetShareInfo1>) : FileState
    data class Downloading(val progress: Int) : FileState
    data class DownloadCompleted(val filePath: String) : FileState
    object Error : FileState
    object Loading : FileState
}
