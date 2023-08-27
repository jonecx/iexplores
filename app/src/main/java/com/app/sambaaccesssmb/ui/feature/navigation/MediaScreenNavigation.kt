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
internal const val shareNamedArg = "shareName"
internal const val smbFilePathArg = "smbFilePath"
internal const val smbFileSizeArg = "smbFileSize"

const val mediaScreenRoute = "mediaScreenRoute"

fun NavController.navigateToMedia(shareName: String, smbFilePath: String, smbFileSize: Long) {
    val encodedShareName = Uri.encode(shareName)
    val encodedSmbFilePath = Uri.encode(smbFilePath)
    val encodedSmbFileSize = Uri.encode(smbFileSize.toString())
    this.navigate("$mediaScreenRoute/$encodedShareName/$encodedSmbFilePath/$encodedSmbFileSize")
}

fun NavGraphBuilder.mediaScreen(
    onBackClick: () -> Unit,
    fileViewModel: FilesViewModel,
) {
    composable(
        route = "$mediaScreenRoute/{$shareNamedArg}/{$smbFilePathArg}/{$smbFileSizeArg}",
        arguments = listOf(
            navArgument(shareNamedArg) { type = NavType.StringType },
            navArgument(smbFilePathArg) { type = NavType.StringType },
            navArgument(smbFileSizeArg) { type = NavType.LongType },
        ),
    ) {
        val shareName = it.arguments?.getString(shareNamedArg).orEmpty()
        val smbFilePath = it.arguments?.getString(smbFilePathArg).orEmpty()
        val smbFileSize = it.arguments?.getLong(smbFileSizeArg) ?: 0L
        MediaRoute(onBackClick = onBackClick, shareName, smbFilePath, smbFileSize, fileViewModel)
    }
}
