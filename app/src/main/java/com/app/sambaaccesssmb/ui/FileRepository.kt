package com.app.sambaaccesssmb.ui

import androidx.annotation.StringRes
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.feature.fvm.FileState
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.CursorState
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.DownloadCompleted
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.NetShareInfoState
import com.app.sambaaccesssmb.utils.DirUtil
import com.app.sambaaccesssmb.utils.getFormattedName
import com.app.sambaaccesssmb.utils.isAvailableLocally
import com.app.sambaaccesssmb.utils.isExcludable
import com.app.sambaaccesssmb.utils.logError
import com.app.sambaaccesssmb.utils.parseSmbPathFromSharePath
import com.app.sambaaccesssmb.utils.shareNameFromPath
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.hierynomus.msfscc.fileinformation.FileStandardInformation
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.share.DiskShare
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo1
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.FileOutputStream
import java.util.EnumSet
import javax.inject.Singleton

data class SmbPath(val path: String = "")

@Singleton
class FileRepository constructor(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    enum class NetShareType(val shareType: Int, @StringRes val description: Int) {
        DISK_TREE(0, R.string.type_drive_or_directory),
        PRINT_QUEUE(1, R.string.type_printer_queue),
        DEVICE(2, R.string.type_device),
        IPC(3, R.string.type_ipc),
    }

    suspend fun getSmbFile(shareName: String, smbFilePath1: String, smbFileSize: Long) = flow<FileState> {
        lateinit var downloadedFilePath: String
        val filePath = "$shareName/$smbFilePath1"
        runCatching {
            DirUtil.getTempFile(filePath.getFormattedName())?.let {
                downloadedFilePath = it.path

                if (downloadedFilePath.isAvailableLocally(smbFileSize)) {
                    emit(DownloadCompleted(downloadedFilePath))
                    return@flow
                }

                val diskShare = getConnectedDiskShare(shareName)
                val buffer = ByteArray(SMBAccess.getSmbSession().session.connection.negotiatedProtocol.maxReadSize)
                val fileOutputStream = FileOutputStream(it)
                val smbFile2bRead = diskShare.openFile(
                    filePath.parseSmbPathFromSharePath(),
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
            it.logError()
            emit(Error)
        }
    }.flowOn(ioDispatcher)

    // TODO: move this to SMBAccess when SMBAccess file kotlin-ized
    private fun getConnectedDiskShare(shareName: String): DiskShare {
        val parsedShareName = shareName.shareNameFromPath()
        SMBAccess.getSmbSession()?.currentDiskShare?.let {
            if (it.isConnected && parsedShareName == it.smbPath.shareName) {
                return it
            } else {
                return@let
            }
        }

        return SMBAccess.getSmbSession().apply {
            currentDiskShare = session.connectShare(parsedShareName) as DiskShare
        }.currentDiskShare
    }

    fun getFileCursor(shareName: String) = flow<FileState> {
        lateinit var smbFiles: List<FileIdBothDirectoryInformation>
        runCatching {
            val smbFilePath = shareName.parseSmbPathFromSharePath()
            smbFiles = getConnectedDiskShare(shareName).list(smbFilePath).filter { !it.isExcludable() }.sortedBy { it.fileName }
        }.onSuccess {
            emit(CursorState(smbFiles))
        }.onFailure {
            it.logError()
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
            it.logError()
            emit(Error)
        }
    }.flowOn(ioDispatcher)
}
