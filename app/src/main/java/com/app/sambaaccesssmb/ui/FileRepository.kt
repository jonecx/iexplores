package com.app.sambaaccesssmb.ui

import androidx.annotation.StringRes
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.feature.fvm.FileState
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.CursorState
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.DownloadCompleted
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Downloading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.NetShareInfoState
import com.app.sambaaccesssmb.utils.DirUtil
import com.app.sambaaccesssmb.utils.getFormattedName
import com.app.sambaaccesssmb.utils.isAvailableLocally
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo1
import jcifs.smb.SmbFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.FileOutputStream
import javax.inject.Singleton

@Singleton
class FileRepository constructor(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val TAG = "FileRepository"

    enum class NetShareType(val shareType: Int, @StringRes val description: Int) {
        DISK_TREE(0, R.string.type_drive_or_directory),
        PRINT_QUEUE(1, R.string.type_printer_queue),
        DEVICE(2, R.string.type_device),
        IPC(3, R.string.type_ipc),
    }

    suspend fun downloadFile(filePath: String) = flow<FileState> {
        val smbFile = SmbFile(filePath, SMBAccess.getSmbConnectionInstance().smbContext)
        runCatching {
            smbFile.let { smbItem ->
                val tempFileName = smbItem.getFormattedName()
                DirUtil.getTempFile(tempFileName)?.let { tempFile ->
                    var downloadedBytes = 0L
                    val tempFilePath = tempFile.path

                    if (smbItem.isAvailableLocally()) {
                        emit(DownloadCompleted(tempFilePath))
                        return@flow
                    }

                    val smbFileSize = smbItem.length()
                    val fileInputStream = smbItem.inputStream
                    val inputStream = BufferedInputStream(fileInputStream)
                    val fileOutputStream = FileOutputStream(tempFile)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead)
                        if (bytesRead > 0) {
                            downloadedBytes += bytesRead
                            if (downloadedBytes > 0) {
                                val progress = (downloadedBytes.toFloat() / smbFileSize * 100).toInt()
                                val distinctProgress = progress.coerceIn(0, 100)
                                emit(Downloading(distinctProgress))
                                delay(30)
                            }
                        }
                    }
                    inputStream.close()
                    fileOutputStream.close()
                    emit(DownloadCompleted(tempFilePath))
                }
            }
        }.getOrElse {
            Timber.d(TAG, it, it.localizedMessage.orEmpty())
            emit(Error)
        }
    }.flowOn(ioDispatcher)

    fun getFileCursor() = flow<FileState> {
        lateinit var smbFiles: List<FileIdBothDirectoryInformation>
        runCatching {
            smbFiles = SMBAccess.getDiskShareInstance().list("")
        }.onSuccess {
            emit(CursorState(smbFiles))
        }.onFailure {
            emit(Error)
        }
    }.flowOn(ioDispatcher)

    fun getNetShares() = flow<FileState> {
        lateinit var netShares: List<NetShareInfo1>
        runCatching {
            netShares = SMBAccess.getSmbSession().serverService.shares1.filter { it.type == NetShareType.DISK_TREE.shareType || it.type == NetShareType.DEVICE.shareType }
        }.onSuccess {
            emit(NetShareInfoState(netShares))
        }.onFailure {
            emit(Error)
        }
    }.flowOn(ioDispatcher)
}
