package com.app.sambaaccesssmb.ui

import com.app.sambaaccesssmb.ui.feature.fvm.FileState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FileDownloadUseCase @Inject constructor(
    private val fileRepository: FileRepository,
) {
    suspend operator fun invoke(shareName: String, smbFilePath: String, smbFileSize: Long): Flow<FileState> = fileRepository.getSmbFile(shareName, smbFilePath, smbFileSize)
}
