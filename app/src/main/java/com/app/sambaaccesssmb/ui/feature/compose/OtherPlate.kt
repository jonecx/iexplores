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
import com.app.sambaaccesssmb.ui.feature.fvm.Locus

@Composable
internal fun OtherPlate(fileItem: Locus) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .padding(4.dp),
            text = fileItem.originalFile.name,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(25.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .align(Alignment.TopEnd),
        ) {
            Text(
                color = MaterialTheme.colorScheme.scrim,
                text = fileItem.originalFile.name.substringAfter("."),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
