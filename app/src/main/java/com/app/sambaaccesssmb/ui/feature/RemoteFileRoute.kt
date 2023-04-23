package com.app.sambaaccesssmb.ui.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun RemoteFileRoute(onNavigateToRemoteFile: () -> Unit) {
    RemoteFileScreen(onNavigateToRemoteFile)
}

@Composable
internal fun RemoteFileScreen(onNavigateToRemoteFile: () -> Unit) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.error)
            .fillMaxSize()
            .clickable(onClick = onNavigateToRemoteFile)
    ) {
        Text(text = "Remote file list")
    }
}
