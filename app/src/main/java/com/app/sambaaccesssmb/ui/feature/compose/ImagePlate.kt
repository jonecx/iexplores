package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.app.sambaaccesssmb.di.ImageLoaderModule
import com.app.sambaaccesssmb.ui.IndeterminateProgressWheel
import com.app.sambaaccesssmb.ui.feature.fvm.Locus
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
internal fun ImagePlate(fileItem: Locus, contentScale: ContentScale = ContentScale.Crop) {
    val imageLoader = ImageLoaderModule.provideImageLoader(LocalContext.current)
    CoilImage(
        modifier = Modifier
            .fillMaxSize(),
        imageModel = { fileItem.originalFile },
        imageLoader = { imageLoader },
        imageOptions = ImageOptions(
            contentScale = contentScale,
            alignment = Alignment.Center,
        ),
        failure = { its ->
            print(its)
        },
        loading = {
            IndeterminateProgressWheel(
                contentDesc = "Loading",
                modifier = Modifier.fillMaxSize(),
            )
        },
    )
}
