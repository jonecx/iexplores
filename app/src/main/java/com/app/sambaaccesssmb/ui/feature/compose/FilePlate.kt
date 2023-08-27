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
internal fun FilePlate(smbItem: FileIdBothDirectoryInformation, shareName: String, onSmbFileClick: (String, String, Long) -> Unit) {
    Column(
        modifier = Modifier
            .size(120.dp)
            .padding(2.dp)
            .clickable { onSmbFileClick(shareName, smbItem.fileName, smbItem.endOfFile) },
    ) {
        val smbFilePath = "$shareName/${smbItem.fileName}"
        when {
            smbItem.isImage() -> ImagePlate(smbFilePath)
            smbItem.isVideo() -> VideoPlate(smbFilePath)
            else -> OtherPlate(smbItem)
        }
    }
}
