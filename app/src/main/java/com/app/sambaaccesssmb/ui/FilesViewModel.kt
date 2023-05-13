package com.app.sambaaccesssmb.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.FileState.Error
import com.app.sambaaccesssmb.ui.FileState.Loading
import com.app.sambaaccesssmb.ui.FileState.Success
import com.app.sambaaccesssmb.utils.capitalizeFirst
import com.app.sambaaccesssmb.utils.itemCount
import dagger.hilt.android.lifecycle.HiltViewModel
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor() : ViewModel() {

    private val sampleFolderName = "Sample"

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
            emit(Error)
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Loading,
        )
}

data class Locus(var fileName: String, val isDirectory: Boolean, val itemCount: String = "", val originalFile: SmbFile)

sealed interface FileState {
    data class Success(val smbFiles: List<Locus>) : FileState
    object Error : FileState
    object Loading : FileState
}
