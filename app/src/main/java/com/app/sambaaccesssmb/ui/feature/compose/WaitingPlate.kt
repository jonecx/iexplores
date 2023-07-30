package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.ui.design.SmbTheme
import com.app.sambaaccesssmb.ui.design.ThemePreviews
import com.app.sambaaccesssmb.ui.feature.fvm.FileState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun Waiting(state: FileState) {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CoilImage(
                imageModel = { R.drawable.app_icon },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                ),
            )
            val messageStringId = when (state) {
                FileState.Loading -> R.string.loading_data
                FileState.Error -> R.string.unknown_error
                else -> R.string.unknown_error
            }
            LabelLargeText(stringResource(id = messageStringId))
        }
    }
}

@ThemePreviews
@Preview("Loading data...")
@Composable
fun WaitingPreview() {
    SmbTheme {
        Column {
            Waiting(FileState.Error)
            Waiting(FileState.Loading)
        }
    }
}
