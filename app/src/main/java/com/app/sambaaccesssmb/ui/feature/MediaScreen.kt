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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.ui.feature.compose.HeadlineLargeText
import com.app.sambaaccesssmb.ui.feature.compose.LabelLargeText
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.DownloadCompleted
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Downloading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Loading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Success
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import jcifs.smb.SmbFile

@Composable
internal fun MediaRoute(
    onBackClick: () -> Unit,
    onMediaClick: (SmbFile) -> Unit,
    mediaUrl: String,
    fileViewModel: FilesViewModel,
) {
    MediaScreen(onBackClick = onBackClick, onMediaClick = onMediaClick, mediaUrl, fileViewModel)
}

@Composable
internal fun MediaScreen(
    onBackClick: () -> Unit,
    onMediaClick: (SmbFile) -> Unit,
    mediaUrl: String,
    filesViewModel: FilesViewModel,
) {
    LaunchedEffect(Unit) {
        filesViewModel.downloadFile(mediaUrl)
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
                    LabelLargeText(stringResource(id = R.string.loading_data))
                }
                is Downloading -> {
                    HeadlineLargeText(displaySmbFileState.progress.toString())
                }
                is DownloadCompleted -> {
                    CoilImage(
                        imageModel = { displaySmbFileState.filePath },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                        ),
                    )
                }
                is Success,
                is Error,
                -> {
                    LabelLargeText(stringResource(id = R.string.unknown_error))
                }
            }
        }
    }
}
