package com.app.sambaaccesssmb.utils

import android.net.InetAddresses.isNumericAddress
import android.os.Build.VERSION_CODES.Q
import android.util.Patterns
import com.hierynomus.msfscc.FileAttributes
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation
import jcifs.smb.SmbFile
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

fun FileIdBothDirectoryInformation.isImage(): Boolean {
    val mimeType = getMimeType(fileName)
    return mimeType.contains("image")
}

fun FileIdBothDirectoryInformation.isGif() = this.isImage() && getMimeType(fileName).contains("gif")

fun FileIdBothDirectoryInformation.isVideo() = getMimeType(fileName).contains("video")

fun SmbFile.getFormattedName() = this.url.path.replace("/", "_")

fun SmbFile.isAvailableLocally(): Boolean {
    val localFile = DirUtil.getTempFile(this.getFormattedName())
    return localFile?.let {
        localFile.isFile && localFile.length() == this.length()
    } ?: false
}
