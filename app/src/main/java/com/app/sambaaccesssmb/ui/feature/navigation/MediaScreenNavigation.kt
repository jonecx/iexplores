package com.app.sambaaccesssmb.ui.feature.navigation

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.sambaaccesssmb.ui.feature.MediaRoute
import com.app.sambaaccesssmb.ui.feature.fvm.FilesViewModel
import jcifs.smb.SmbFile

@VisibleForTesting
internal const val mediaIdArg = "mediaId"

const val mediaScreenRoute = "mediaScreenRoute"

fun NavController.navigateToMedia(smbFile: SmbFile) {
    val encodedMediaId = Uri.encode(smbFile.path)
    this.navigate("$mediaScreenRoute/$encodedMediaId")
}

fun NavGraphBuilder.mediaScreen(
    onBackClick: () -> Unit,
    onMediaClick: (SmbFile) -> Unit,
    fileViewModel: FilesViewModel,
) {
    composable(
        route = "$mediaScreenRoute/{$mediaIdArg}",
        arguments = listOf(
            navArgument(mediaIdArg) { type = NavType.StringType },
        ),
    ) {
        MediaRoute(onBackClick = onBackClick, onMediaClick = onMediaClick, fileViewModel)
    }
}
