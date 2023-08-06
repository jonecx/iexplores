package com.app.sambaaccesssmb.ui.feature.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.sambaaccesssmb.ui.feature.fvm.Locus

@Composable
internal fun VideoPlate(fileItem: Locus) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxSize(),
    ) {
        ImagePlate(fileItem = fileItem)
        Icon(
            Filled.PlayCircle,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp),
        )
    }
}
