package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.ui.feature.fvm.Locus
import com.app.sambaaccesssmb.utils.isImage
import com.app.sambaaccesssmb.utils.isVideo

@Composable
internal fun FilePlate(fileItem: Locus, onMediaClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .size(120.dp)
            .padding(2.dp)
            .clickable { onMediaClick(fileItem.originalFile.path) },
    ) {
        when {
            fileItem.originalFile.isImage() -> ImagePlate(fileItem = fileItem)
            fileItem.originalFile.isVideo() -> VideoPlate(fileItem)
            else -> OtherPlate(fileItem)
        }
    }
}
