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
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Loading
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel
import com.app.sambaaccesssmb.ui.feature.fvm.Locus
import jcifs.smb.SmbFile

@Composable
internal fun RemoteFileRoute(onNavigateToRemoteFile: () -> Unit, onMediaClick: (SmbFile) -> Unit, fileViewModel: FilesViewModel) {
    RemoteFileScreen(onNavigateToRemoteFile, onMediaClick, fileViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RemoteFileScreen(
    onNavigateToHomeScreen: () -> Unit,
    onMediaClick: (SmbFile) -> Unit,
    filesViewModel: FilesViewModel,
) {
    val fileCursor = filesViewModel.fileCursor.collectAsStateWithLifecycle().value

    if (SMBAccess.getSmbConnectionInstance().isConnectionStabled) {
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
                    when (fileCursor) {
                        is FileState.Error,
                        is FileState.Loading,
                        -> Waiting(fileCursor)
                        is FileState.Success -> SmbItemGrid(fileCursor.smbFiles, onMediaClick)
                        else -> Waiting(fileCursor)
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
internal fun SmbItemGrid(fileItems: List<Locus>, onMediaClick: (SmbFile) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = PaddingValues(1.dp),
    ) {
        items(fileItems) { item ->
            when (item.isDirectory) {
                true -> DirectoryPlate(directoryItem = item)
                else -> FilePlate(fileItem = item, onMediaClick)
            }
        }
    }
}
