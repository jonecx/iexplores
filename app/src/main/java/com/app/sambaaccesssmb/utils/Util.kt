package com.app.sambaaccesssmb.utils

import android.net.InetAddresses.isNumericAddress
import android.os.Build.VERSION_CODES.Q
import android.util.Patterns
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import timber.log.Timber
import java.io.File
import java.net.URLConnection
import java.util.Locale

object DirUtil {
    fun getTempFile(tempFileName: String): File? {
        return runCatching {
            val dir = System.getProperty("java.io.tmpdir", ".")?.let { File(it) }
            val f = File(dir, tempFileName)
            return f
        }.getOrElse {
            Timber.d(it, it.localizedMessage.orEmpty())
            null
        }
    }
}

fun FileIdBothDirectoryInformation.isDirectory(): Boolean {
    return (fileAttributes and FileAttributes.FILE_ATTRIBUTE_DIRECTORY.value) != 0L
}

fun FileIdBothDirectoryInformation.isHidden(): Boolean {
    return (fileAttributes and FileAttributes.FILE_ATTRIBUTE_HIDDEN.value) != 0L
}

fun FileIdBothDirectoryInformation.isExcludable(): Boolean {
    return fileName.startsWith(".") && shortName.isBlank() || isHidden()
}

fun String?.isValidAddress(): Boolean {
    return this?.let {
        this.isValidText() && if (Build.has(Q)) isNumericAddress(this) else Patterns.IP_ADDRESS.matcher(this).matches()
    } ?: false
}

fun String?.isValidUsername() = this.isValidText()

fun String?.isValidPassword() = this.isValidText()

fun String?.isValidText() = !this.isNullOrEmpty()

fun String.capitalizeFirst() = this.replaceFirstChar { firstLetter -> firstLetter.titlecase(Locale.getDefault()) }

private fun getMimeType(path: String) = URLConnection.guessContentTypeFromName(path).orEmpty()

fun FileIdBothDirectoryInformation.isImage(): Boolean {
    val mimeType = getMimeType(fileName)
    return mimeType.contains("image")
}

fun FileIdBothDirectoryInformation.isGif() = this.isImage() && getMimeType(fileName).contains("gif")

fun FileIdBothDirectoryInformation.isVideo() = getMimeType(fileName).contains("video")

fun String.isImage(): Boolean {
    val mimeType = getMimeType(this)
    return mimeType.contains("image")
}

fun String.isGif() = this.isImage() && getMimeType(this).contains("gif")

fun String.isVideo() = getMimeType(this).contains("video")

fun String.getFormattedName() = this.replace("/", "_")

fun String.isAvailableLocally(smbFileSize: Long): Boolean {
    val localFile = File(this)
    return localFile.isFile && localFile.length() == smbFileSize
}

fun String.shareNameFromPath(): String {
    return this.substringBefore("/")
}

fun String.parseSmbPathFromSharePath(): String {
    return this.takeIf { it.contains("/") }?.substringAfter("/").orEmpty()
}

fun Throwable.logError() {
    Timber.d(this, this.localizedMessage.orEmpty())
}
