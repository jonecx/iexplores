package com.app.sambaaccesssmb.ui.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.ui.feature.compose.HeadlineLargeText
import com.app.sambaaccesssmb.ui.feature.compose.LabelLargeText
import com.app.sambaaccesssmb.ui.feature.compose.ZoomableImagePlate
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.DownloadCompleted
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Downloading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Loading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Success
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel

@Composable
internal fun MediaRoute(
    onBackClick: () -> Unit,
    shareName: String,
    smbFilePath: String,
    smbFileSize: Long,
    fileViewModel: FilesViewModel,
) {
    MediaScreen(onBackClick = onBackClick, shareName, smbFilePath, smbFileSize, fileViewModel)
}

@Composable
internal fun MediaScreen(
    onBackClick: () -> Unit,
    shareName: String,
    smbFilePath: String,
    smbFileSize: Long,
    filesViewModel: FilesViewModel,
) {
    LaunchedEffect(Unit) {
        filesViewModel.getSmbFile(shareName, smbFilePath, smbFileSize)
    }

    val displaySmbFileState = filesViewModel.fileDownloadState.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp, bottom = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (displaySmbFileState) {
                is Loading -> {
                    LabelLargeText(text = stringResource(id = R.string.loading_data))
                }
                is Downloading -> {
                    HeadlineLargeText(text = displaySmbFileState.progress.toString())
                }
                is DownloadCompleted -> {
                    ZoomableImagePlate(mediaUrl = displaySmbFileState.filePath)
                }
                is Success,
                is Error,
                -> {
                    LabelLargeText(text = stringResource(id = R.string.unknown_error))
                }
                else -> {
                    LabelLargeText(text = stringResource(id = R.string.unknown_error))
                }
            }
        }
    }
}
