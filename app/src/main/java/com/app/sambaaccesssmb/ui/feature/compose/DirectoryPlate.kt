package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation

@Composable
internal fun DirectoryPlate(smbItem: FileIdBothDirectoryInformation, shareName: String, onDirectoryClick: (String) -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .size(120.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onDirectoryClick("$shareName/${smbItem.fileName}") },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = smbItem.fileName,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                Filled.Folder,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(25.dp),
            )
        }
    }
}
