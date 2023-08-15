package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.di.ImageLoaderModule
import com.app.sambaaccesssmb.ui.IndeterminateProgressWheel
import com.app.sambaaccesssmb.utils.isGif
import com.app.sambaaccesssmb.utils.isImage
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
internal fun ImagePlate(smbItem: FileIdBothDirectoryInformation, contentScale: ContentScale = ContentScale.Crop) {
    val imageLoader = ImageLoaderModule.provideImageLoader(LocalContext.current)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxSize(),
    ) {
        CoilImage(
            modifier = Modifier.fillMaxSize(),
            imageModel = { smbItem.fileName },
            imageLoader = { imageLoader },
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center,
            ),
            failure = {},
            loading = {
                IndeterminateProgressWheel(
                    contentDesc = "Loading",
                    modifier = Modifier.fillMaxSize(),
                )
            },
        )
        if (smbItem.isGif() || smbItem.isImage()) {
            Icon(
                Filled.Image,
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center)
                    .size(56.dp),
            )
        }
    }
}
