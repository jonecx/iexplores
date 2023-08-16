package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.utils.isImage
import com.app.sambaaccesssmb.utils.isVideo
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation

@Composable
internal fun FilePlate(smbItem: FileIdBothDirectoryInformation, shareName: String, onMediaClick: (String, String, Long) -> Unit) {
    Column(
        modifier = Modifier
            .size(120.dp)
            .padding(2.dp)
            .clickable { onMediaClick(shareName, smbItem.fileName, smbItem.endOfFile) },
    ) {
        when {
            smbItem.isImage() -> ImagePlate(smbItem = smbItem)
            smbItem.isVideo() -> VideoPlate(smbItem)
            else -> OtherPlate(smbItem)
        }
    }
}
