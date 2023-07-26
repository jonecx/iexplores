package com.app.sambaaccesssmb.utils

import android.net.InetAddresses.isNumericAddress
import android.os.Build.VERSION_CODES.Q
import android.util.Patterns
import jcifs.smb.SmbFile
import java.net.URLConnection
import java.util.Locale

fun String?.isValidAddress(): Boolean {
    return this?.let {
        this.isValidText() && if (Build.has(Q)) isNumericAddress(this) else Patterns.IP_ADDRESS.matcher(this).matches()
    } ?: false
}

fun String?.isValidUsername() = this.isValidText()

fun String?.isValidPassword() = this.isValidText()

fun String?.isValidText() = !this.isNullOrEmpty()

fun String.capitalizeFirst() = this.replaceFirstChar { firstLetter -> firstLetter.titlecase(Locale.getDefault()) }

fun SmbFile.itemCount(directory: Boolean): String {
    return if (directory) {
        runCatching {
            this.listFiles().size.toString()
        }.getOrElse {
            ""
        }
    } else {
        ""
    }
}

private fun getMimeType(path: String) = URLConnection.guessContentTypeFromName(path).orEmpty()

fun SmbFile.isImage(): Boolean {
    val mimeType = getMimeType(this.path)
    return mimeType.contains("image")
}

fun SmbFile.isGif() = this.isImage() && getMimeType(this.path).contains("gif")

fun SmbFile.isVideo() = getMimeType(this.path).contains("video")
