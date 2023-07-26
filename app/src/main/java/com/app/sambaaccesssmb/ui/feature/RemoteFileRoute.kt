package com.app.sambaaccesssmb.ui.feature

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayCircle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.di.ImageLoaderModule
import com.app.sambaaccesssmb.ui.FileState
import com.app.sambaaccesssmb.ui.FileState.Loading
import com.app.sambaaccesssmb.ui.FilesViewModel
import com.app.sambaaccesssmb.ui.IndeterminateProgressWheel
import com.app.sambaaccesssmb.ui.Locus
import com.app.sambaaccesssmb.utils.isImage
import com.app.sambaaccesssmb.utils.isVideo
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
internal fun RemoteFileRoute(onNavigateToRemoteFile: () -> Unit) {
    RemoteFileScreen(onNavigateToRemoteFile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RemoteFileScreen(
    onNavigateToHomeScreen: () -> Unit,
    filesViewModel: FilesViewModel = hiltViewModel(),
) {
    val fileCursor = filesViewModel.fileCursor.collectAsStateWithLifecycle().value
    val context = LocalContext.current

    if (SMBAccess.getSmbConnectionInstance().isConnectionStabled) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Testing stuff") },
                    navigationIcon = {
                        IconButton(onClick = {
                            Toast.makeText(context, "Navigation Icon Click", Toast.LENGTH_SHORT)
                                .show()
                        }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Navigation icon")
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
                        is Error,
                        is Loading,
                        -> {
                        }

                        is FileState.Success -> SmbItemGrid(fileCursor.smbFiles)
                        else -> {}
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
internal fun DirectoryItem(directoryItem: Locus) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .size(120.dp)
            .padding(2.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable {
                Toast
                    .makeText(context, directoryItem.originalFile.name, Toast.LENGTH_SHORT)
                    .show()
            },
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier
                    .padding(4.dp),
                text = directoryItem.fileName,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                Icons.Filled.Folder,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(25.dp),
            )
        }
    }
}

@Composable
internal fun SmbItemGrid(fileItems: List<Locus>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        contentPadding = PaddingValues(1.dp),
    ) {
        items(fileItems) { item ->
            when (item.isDirectory) {
                true -> DirectoryItem(directoryItem = item)
                else -> FileItem(fileItem = item)
            }
        }
    }
}

@Composable
internal fun FileItem(fileItem: Locus) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .size(120.dp)
            .padding(2.dp)
            .clickable {
                Toast
                    .makeText(context, fileItem.originalFile.name, Toast.LENGTH_SHORT)
                    .show()
            },
    ) {
        when {
            fileItem.originalFile.isImage() -> ImagePlate(fileItem = fileItem)
            fileItem.originalFile.isVideo() -> VideoPlate(fileItem)
            else -> OtherPlate(fileItem)
        }
    }
}

@Composable
internal fun ImagePlate(fileItem: Locus) {
    val imageLoader = ImageLoaderModule.provideImageLoader(LocalContext.current)
    CoilImage(
        modifier = Modifier
            .fillMaxSize(),
        imageModel = { fileItem.originalFile },
        imageLoader = { imageLoader },
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        ),
        failure = { its ->
            print(its)
        },
        loading = {
            IndeterminateProgressWheel(
                contentDesc = "Loading",
                modifier = Modifier.fillMaxSize(),
            )
        },
    )
}

@Composable
internal fun VideoPlate(fileItem: Locus) {
    Box(modifier = Modifier.fillMaxSize()) {
        ImagePlate(fileItem = fileItem)
        Icon(
            Icons.Filled.PlayCircle,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp),
        )
    }
}

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
