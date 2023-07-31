package com.app.sambaaccesssmb.ui

import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.feature.fvm.FileState
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.DownloadCompleted
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Downloading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.utils.DirUtil
import com.app.sambaaccesssmb.utils.getFormattedName
import com.app.sambaaccesssmb.utils.isAvailableLocally
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
}
