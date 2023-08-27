package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation

@Composable
internal fun OtherPlate(smbItem: FileIdBothDirectoryInformation) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .padding(4.dp),
            text = smbItem.fileName,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(25.dp)
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .align(Alignment.TopEnd),
        ) {
            Text(
                color = MaterialTheme.colorScheme.surfaceVariant,
                text = smbItem.fileName.substringAfter(".").lowercase(),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
