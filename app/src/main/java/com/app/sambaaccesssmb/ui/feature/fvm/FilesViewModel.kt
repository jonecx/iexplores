package com.app.sambaaccesssmb.ui.feature.fvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Downloading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.DownloadingSuccess
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Loading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Success
import com.app.sambaaccesssmb.utils.DirUtil
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
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor() : ViewModel() {

    private val sampleFolderName = "Sample"
    var mediaId = ""

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

    val smbs: StateFlow<FileState> = flow<FileState> {
        var progress = 0
        runCatching {
            val cifsContext = SMBAccess.getCIFSContext("smb://10.4.204.193", "spinner", "plYC_0eXD1p_GDZ")
            val smbFile = SmbFile("smb://10.4.204.193/Radioactivity/Sample/Sniff Petrol/", cifsContext)

            val smbFilez = smbFile.listFiles().filter { it.name == "abirdie.jpg" }
            print(smbFilez)
            val fis = smbFilez[0].inputStream
            val inputStream = BufferedInputStream(fis)
            val temp = DirUtil.getTempFile("abirdie.jpg")
            val pt = temp?.path
            print(pt)
            val fileSize = smbFilez[0].length()
            val fileOutputStream = FileOutputStream(temp)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
                if (bytesRead > 0) {
                    progress += bytesRead
                    if (progress > 0) {
                        val x = progress.toFloat().div(fileSize)
                        print(x)
                        val p = x * 100
                        emit(Downloading(p.toInt()))
                    }
                }
            }

            inputStream.close()
            fileOutputStream.close()
            emit(DownloadingSuccess(pt!!))
        }.getOrElse {
            Timber.d(it, it.localizedMessage.orEmpty())
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
    data class Downloading(val progress: Int) : FileState
    data class DownloadingSuccess(val progress: String) : FileState
    object Error : FileState
    object Loading : FileState
}
