package com.app.sambaaccesssmb.ui.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.feature.compose.DirectoryPlate
import com.app.sambaaccesssmb.ui.feature.compose.FilePlate
import com.app.sambaaccesssmb.ui.feature.compose.Waiting
import com.app.sambaaccesssmb.ui.feature.fvm.FileState
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel
import com.app.sambaaccesssmb.utils.isDirectory
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation

@Composable
internal fun RemoteFileRoute(onNavigateToRemoteFile: () -> Unit, onMediaClick: (String) -> Unit, shareName: String, fileViewModel: FilesViewModel) {
    RemoteFileScreen(onNavigateToRemoteFile, onMediaClick, shareName, fileViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RemoteFileScreen(
    onNavigateToHomeScreen: () -> Unit,
    onMediaClick: (String) -> Unit,
    shareName: String,
    filesViewModel: FilesViewModel,
) {
    if (SMBAccess.getSmbSession()?.isConnected == true) {
        LaunchedEffect(Unit) {
            filesViewModel.getFileCursor(shareName)
        }
        val fileCursorState = filesViewModel.fileCursorState.collectAsStateWithLifecycle().value
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name_rebrand)) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = stringResource(id = R.string.back_button))
                        }
                    },
                    windowInsets = WindowInsets(
                        top = 0.dp,
                        bottom = 0.dp,
                    ),
                    colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface, titleContentColor = MaterialTheme.colorScheme.onSurface),
                )
            },
            content = {
                Column(modifier = Modifier.padding(it)) {
                    when (fileCursorState) {
                        is FileState.CursorState -> SmbItemGrid(fileCursorState.smbItems, shareName, onMediaClick)
                        is FileState.Error,
                        is FileState.Loading,
                        -> Waiting(fileCursorState)
                        else -> Waiting(fileCursorState)
                    }
                }
            },
        )
    } else {
        LaunchedEffect(Unit) {
            onNavigateToHomeScreen.invoke()
        }
    }
}

@Composable
internal fun SmbItemGrid(smbItems: List<FileIdBothDirectoryInformation>, shareName: String, onMediaClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = PaddingValues(1.dp),
    ) {
        items(smbItems) { item ->
            when (item.isDirectory()) {
                true -> DirectoryPlate(smbItem = item)
                else -> FilePlate(smbItem = item, shareName, onMediaClick)
            }
        }
    }
}
