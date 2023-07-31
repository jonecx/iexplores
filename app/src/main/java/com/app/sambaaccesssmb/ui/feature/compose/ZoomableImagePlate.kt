package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ZoomableImagePlate(
    mediaUrl: String,
    contentDescription: String? = null,
) {
    val zoomState = rememberZoomState()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(ImageDecoderDecoder.Factory())
        }
        .build()
    AsyncImage(
        model = mediaUrl,
        imageLoader = imageLoader,
        contentDescription = contentDescription.orEmpty(),
        contentScale = ContentScale.Fit,
        onSuccess = {
            zoomState.setContentSize(it.painter.intrinsicSize)
        },
        modifier = Modifier
            .fillMaxSize()
            .zoomable(
                zoomState = zoomState,
                onDoubleTap = { position ->
                    val targetScale = when {
                        zoomState.scale < 2f -> 2f
                        zoomState.scale < 4f -> 4f
                        else -> 1f
                    }
                    zoomState.changeScale(targetScale, position)
                },
            ),
    )
}
