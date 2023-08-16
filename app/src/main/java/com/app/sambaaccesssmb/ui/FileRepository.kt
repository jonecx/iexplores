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
import com.app.sambaaccesssmb.utils.isExcludable
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.msfscc.fileinformation.FileStandardInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.share.DiskShare
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo1
import jcifs.internal.smb2.create.Smb2CreateRequest.FILE_SHARE_READ
import jcifs.smb.SmbFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.util.EnumSet
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

    suspend fun getSmbFile(shareName: String, smbFilePath: String, smbFileSize: Long) = flow<FileState> {
        lateinit var downloadedFilePath: String
        runCatching {
            DirUtil.getTempFile(smbFilePath.getFormattedName())?.let {
                downloadedFilePath = it.path

                if (downloadedFilePath.isAvailableLocally(smbFileSize)) {
                    emit(DownloadCompleted(downloadedFilePath))
                    return@flow
                }

                val diskShare = SMBAccess.getSmbSession().session.connectShare(shareName) as DiskShare
                val buffer = ByteArray(SMBAccess.getSmbSession().session.connection.negotiatedProtocol.maxReadSize)
                val fileOutputStream = FileOutputStream(it)
                val smbFile2bRead = diskShare.openFile(
                    smbFilePath.substringAfterLast("/"),
                    EnumSet.of(AccessMask.GENERIC_READ),
                    null,
                    setOf(SMB2ShareAccess.FILE_SHARE_READ),
                    SMB2CreateDisposition.FILE_OPEN,
                    null,
                )

                var offset: Long = 0
                var remaining: Long = smbFile2bRead.getFileInformation(FileStandardInformation::class.java).endOfFile
                while (remaining > 0) {
                    val amountToRead = if (remaining > buffer.size) buffer.size else remaining.toInt()
                    val amountRead = smbFile2bRead.read(buffer, offset, 0, amountToRead)
                    if (amountToRead == -1) {
                        remaining = 0
                    } else {
                        fileOutputStream.write(buffer, 0, amountRead)
                        remaining -= amountRead
                        offset += amountRead
                    }
                }
            }
        }.onSuccess {
            emit(DownloadCompleted(downloadedFilePath))
        }.onFailure {
            Timber.d(TAG, it, it.localizedMessage.orEmpty())
            emit(Error)
        }
    }.flowOn(ioDispatcher)

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

    fun getFileCursor(shareName: String) = flow<FileState> {
        lateinit var smbFiles: List<FileIdBothDirectoryInformation>
        runCatching {
            val diskShare = SMBAccess.getSmbSession().session.connectShare(shareName) as DiskShare
            smbFiles = diskShare.list("").filter { !it.isExcludable() }
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
