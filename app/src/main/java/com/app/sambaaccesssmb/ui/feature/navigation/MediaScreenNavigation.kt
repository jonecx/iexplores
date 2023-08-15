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

@VisibleForTesting
internal const val mediaIdArg = "mediaId"

const val mediaScreenRoute = "mediaScreenRoute"

fun NavController.navigateToMedia(smbFile: String) {
    val encodedMediaId = Uri.encode(smbFile)
    this.navigate("$mediaScreenRoute/$encodedMediaId")
}

fun NavGraphBuilder.mediaScreen(
    onBackClick: () -> Unit,
    onMediaClick: (String) -> Unit,
    fileViewModel: FilesViewModel,
) {
    composable(
        route = "$mediaScreenRoute/{$mediaIdArg}",
        arguments = listOf(
            navArgument(mediaIdArg) { type = NavType.StringType },
        ),
    ) {
        val mediaUrl = it.arguments?.getString(mediaIdArg).orEmpty()
        MediaRoute(onBackClick = onBackClick, onMediaClick = onMediaClick, mediaUrl, fileViewModel)
    }
}
