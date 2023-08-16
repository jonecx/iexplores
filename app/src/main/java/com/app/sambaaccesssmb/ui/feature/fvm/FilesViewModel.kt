package com.app.sambaaccesssmb.ui.feature.fvm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.FileCursorUseCase
import com.app.sambaaccesssmb.ui.FileDownloadUseCase
import com.app.sambaaccesssmb.ui.NetShareUseCase
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Loading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Success
import com.app.sambaaccesssmb.utils.capitalizeFirst
import com.app.sambaaccesssmb.utils.itemCount
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo1
import dagger.hilt.android.lifecycle.HiltViewModel
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor(private val fileDownloadUseCase: FileDownloadUseCase, private val fileCursorUseCase: FileCursorUseCase, private val netShareUseCase: NetShareUseCase) : ViewModel() {

    companion object {
        private const val TAG = "FilesViewModel"
    }

    private val sampleFolderName = "Sample"

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

    val fileCursor: StateFlow<FileState> = flow<FileState> {
        runCatching {
            val smbFiles = SMBAccess.getSmbConnectionInstance().rootSMBFile.listFiles()
            val subList = smbFiles[1].listFiles() { file -> file.name.contains(sampleFolderName) && file.isDirectory }

            val firstSmbFolder = subList[0].listFiles().filterNot { it.name.startsWith(".") }
            val smbItems = firstSmbFolder.sortedByDescending { it.createTime() }.map { smbFile ->
                val isDirectory = smbFile.isDirectory
                val itemCountInside = smbFile.itemCount(isDirectory)
                Locus(smbFile.name.capitalizeFirst().dropLast(1), isDirectory, itemCountInside, smbFile)
            }.toList()
            emit(Success(smbItems))
        }.getOrElse {
            Timber.d(TAG, it, it.localizedMessage.orEmpty())
            emit(Error)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )

/*    fun downloadFile(filePath: String) {
        viewModelScope.launch {
            fileDownloadUseCase(filePath).collectLatest {
                _fileDownloadState.value = it
            }
        }
    }*/

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

data class Locus(var fileName: String, val isDirectory: Boolean, val itemCount: String = "", val originalFile: SmbFile)

sealed interface FileState {
    data class Success(val smbFiles: List<Locus>) : FileState
    data class CursorState(val smbItems: List<FileIdBothDirectoryInformation>) : FileState
    data class NetShareInfoState(val netShares: List<NetShareInfo1>) : FileState
    data class Downloading(val progress: Int) : FileState
    data class DownloadCompleted(val filePath: String) : FileState
    object Error : FileState
    object Loading : FileState
}
