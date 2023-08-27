package com.app.sambaaccesssmb.ui.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells.Adaptive
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons.Filled
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
import com.app.sambaaccesssmb.R.string
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.feature.compose.NetSharePlate
import com.app.sambaaccesssmb.ui.feature.compose.Waiting
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Error
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.Loading
import com.app.sambaaccesssmb.ui.feature.fvm.FileState.NetShareInfoState
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel
import com.rapid7.client.dcerpc.mssrvs.dto.NetShareInfo1

@Composable
internal fun NetShareRoute(onNavigateToHomeScreen: () -> Unit, onShareClick: (String) -> Unit, fileViewModel: FilesViewModel) {
    NetShareScreen(onNavigateToHomeScreen, onShareClick, fileViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NetShareScreen(
    onNavigateToHomeScreen: () -> Unit,
    onShareClick: (String) -> Unit,
    filesViewModel: FilesViewModel,
) {
    if (SMBAccess.getSmbSession()?.isConnected == true) {
        val netShareState = filesViewModel.netShareState.collectAsStateWithLifecycle().value
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = string.app_name_rebrand)) },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Filled.Menu, contentDescription = stringResource(id = string.back_button))
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
                    when (netShareState) {
                        is Error,
                        is Loading,
                        -> Waiting(netShareState)
                        is NetShareInfoState -> NetShareGrid(netShareState.netShares, onShareClick)
                        else -> Waiting(netShareState)
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
internal fun NetShareGrid(netShareItems: List<NetShareInfo1>, onShareClick: (String) -> Unit) {
    LazyVerticalGrid(
        columns = Adaptive(120.dp),
        contentPadding = PaddingValues(1.dp),
    ) {
        items(netShareItems) { item ->
            NetSharePlate(netShare = item, onShareClick)
        }
    }
}
