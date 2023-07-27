package com.app.sambaaccesssmb.ui.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.app.sambaaccesssmb.ui.feature.fvm.FileState
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Downloading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Success
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import jcifs.smb.SmbFile

@Composable
internal fun MediaRoute(
    onBackClick: () -> Unit,
    onMediaClick: (SmbFile) -> Unit,
    fileViewModel: FilesViewModel,
) {
    MediaScreen(onBackClick = onBackClick, onMediaClick = onMediaClick, fileViewModel)
}

@Composable
internal fun MediaScreen(
    onBackClick: () -> Unit,
    onMediaClick: (SmbFile) -> Unit,
    filesViewModel: FilesViewModel,
) {
    val displaySmbFileState = filesViewModel.displaySmbFile.collectAsState(initial = FileState.Loading).value
    Column(
        modifier = Modifier
            .background(Color.Red)
            .fillMaxSize(),
    ) {
        when (displaySmbFileState) {
            is FileState.Success -> {
                Column(
                    modifier = Modifier
                        .background(Color.Cyan)
                        .fillMaxSize(),
                ) {
                }
            }
            is FileState.Downloading -> {
                print(displaySmbFileState.progress)
            }
            is FileState.DownloadCompleted -> CoilImage(
                imageModel = { displaySmbFileState.filePath },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                ),
            )
            is FileState.Loading -> {
                Column(
                    modifier = Modifier
                        .background(Color.Yellow)
                        .fillMaxSize(),
                ) {
                }
            }
            is FileState.Error -> {
                Column(
                    modifier = Modifier
                        .background(Color.Green)
                        .fillMaxSize(),
                ) {
                }
            }
        }
    }
}
