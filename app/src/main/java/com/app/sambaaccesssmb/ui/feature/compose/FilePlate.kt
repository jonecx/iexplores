package com.app.sambaaccesssmb.ui.feature.compose

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.ui.feature.fvm.Locus
import com.app.sambaaccesssmb.utils.isImage
import com.app.sambaaccesssmb.utils.isVideo
import jcifs.smb.SmbFile

@Composable
internal fun FilePlate(fileItem: Locus, onMediaClick: (SmbFile) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .size(120.dp)
            .padding(2.dp)
            .clickable {
                       Toast.makeText(context, fileItem.fileName, Toast.LENGTH_SHORT).show()
//                onMediaClick(fileItem.originalFile)
                       },
    ) {
        when {
            fileItem.originalFile.isImage() -> ImagePlate(fileItem = fileItem)
            fileItem.originalFile.isVideo() -> VideoPlate(fileItem)
            else -> OtherPlate(fileItem)
        }
    }
}
