package com.app.sambaaccesssmb.ui.feature

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sambaaccesssmb.ui.feature.compose.ImagePlate
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Downloading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.DownloadingSuccess
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Success
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel

@Composable
internal fun MediaRoute(
    onBackClick: () -> Unit,
    onMediaClick: (String) -> Unit,
    fileViewModel: FilesViewModel,
) {
    MediaScreen(onBackClick = onBackClick, onMediaClick = onMediaClick, fileViewModel)
}

@Composable
internal fun MediaScreen(
    onBackClick: () -> Unit,
    onMediaClick: (String) -> Unit,
    filesViewModel: FilesViewModel,
) {
    val fileCursor = filesViewModel.fileCursor.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .background(Color.Red)
            .fillMaxSize(),
    ) {
        when (fileCursor) {
            is Success -> {
                ImagePlate(fileItem = fileCursor.smbFiles.find { it.originalFile.path == filesViewModel.mediaId }!!, ContentScale.Fit)
                when (val xyz = filesViewModel.smbs.collectAsState().value) {
                    is Downloading -> Toast.makeText(LocalContext.current, "Downloading...${xyz.progress}", Toast.LENGTH_SHORT).show()
                    is DownloadingSuccess -> ""
                    /*GlideImage(
                        imageModel = { xyz.progress}, // loading a network image using an URL.
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        ),
                    )*/
                    else -> print("stuff")
                }
            }
            else -> Column(
                modifier = Modifier
                    .background(Color.Green)
                    .fillMaxSize(),
            ) {
            }
        }
    }
}
