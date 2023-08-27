package com.app.sambaaccesssmb.di

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.decode.ContentMetadata
import coil.decode.DataSource.DISK
import coil.decode.DataSource.MEMORY
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.ImageSource
import coil.decode.VideoFrameDecoder
import coil.disk.DiskCache
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.app.sambaaccesssmb.R
import com.app.sambaaccesssmb.R.drawable
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.SmbPath
import com.app.sambaaccesssmb.utils.Build
import com.app.sambaaccesssmb.utils.DirUtil
import com.app.sambaaccesssmb.utils.getFormattedName
import com.app.sambaaccesssmb.utils.isImage
import com.app.sambaaccesssmb.utils.isVideo
import com.app.sambaaccesssmb.utils.logError
import com.app.sambaaccesssmb.utils.parseSmbPathFromSharePath
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.share.File
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import okio.buffer
import okio.source
import wseemann.media.FFmpegMediaMetadataRetriever
import java.io.FileOutputStream
import java.util.EnumSet

@Module
@InstallIn(ActivityComponent::class)
object ImageLoaderModule {
    @Provides
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.apply { mkdirs() })
                    .maxSizePercent(0.05)
                    .cleanupDispatcher(Dispatchers.IO)
                    .build()
            }
            .components {
                add(SmbFileFetcher.Factory())
                add(VideoFrameDecoder.Factory())
                if (Build.has(P)) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .fallback(drawable.ic_file)
            .build()
    }
}

class SmbFileFetcher(
    private val smbPath: SmbPath,
    private val options: Options,
) : Fetcher {

    private val TAG = "SmbFileFetcher"

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult {
        val truePath = if (SMBAccess.isFeatureHidden && smbPath.path.isImage()) "Zuckerberg_hockey.jpg" else smbPath.path.parseSmbPathFromSharePath()
        val smbFile2bRead = SMBAccess.getSmbSession().currentDiskShare.openFile(
            truePath,
            EnumSet.of(AccessMask.GENERIC_READ),
            null,
            setOf(SMB2ShareAccess.FILE_SHARE_READ),
            SMB2CreateDisposition.FILE_OPEN,
            null,
        )

        val inputStream = smbFile2bRead.inputStream
        val tempFileName = smbPath.path.getFormattedName()

        return if (smbPath.path.isVideo()) {
            DrawableResult(
                drawable = BitmapDrawable(options.context.resources, getVideoThumbnail(tempFileName, smbFile2bRead)),
                isSampled = false,
                dataSource = MEMORY,
            )
        } else {
            SourceResult(
                source = ImageSource(
                    source = inputStream.source().buffer(),
                    context = options.context,
                    metadata = ContentMetadata(Uri.parse(smbPath.path)),
                ),
                mimeType = null,
                dataSource = DISK,
            )
        }
    }

    private fun getVideoThumbnail(fileName: String, file: File): Bitmap? {
        var frame: Bitmap? = null
        return runCatching {
            // TODO CoilImage smb client video thumbnail support (check if temp file is around: reuse)
            val tempFile = DirUtil.getTempFile(fileName)!!

            val fileSize = file.fileInformation.standardInformation.endOfFile

            val trueSize = if (fileSize < 5000000L) {
                fileSize
            } else {
                (fileSize / 100)
            }

            if (!tempFile.exists()) {
                val inputStream = file.inputStream
                val outputStream = FileOutputStream(tempFile)
                val buffer = ByteArray(4 * 1024 * 1024)
                var bytesRead = inputStream.read(buffer)

                var totalBytesRead = 0
                while (bytesRead != -1 && totalBytesRead <= trueSize) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    bytesRead = inputStream.read(buffer)
                }
                inputStream.close()
            }

            val retriever = FFmpegMediaMetadataRetriever()
            val frameAt = 0L

            retriever.setDataSource(tempFile.path)

            frame = retriever.getFrameAtTime(frameAt)

            retriever.release()
            handleBitmapError(frame == null)
        }.getOrElse {
            it.logError()
            handleBitmapError()
        }
    }

    private fun handleBitmapError(showErrorIcon: Boolean = true): Bitmap? {
        @DrawableRes val icon: Int = if (showErrorIcon) R.drawable.ic_error else R.drawable.ic_video
        val drawable: Drawable = ContextCompat.getDrawable(options.context, icon)!!
        return drawable.toBitmapOrNull()
    }

    class Factory : Fetcher.Factory<SmbPath> {

        override fun create(data: SmbPath, options: Options, imageLoader: ImageLoader): Fetcher? {
            if (!isApplicable(data)) return null
            return SmbFileFetcher(data, options)
        }

        private fun isApplicable(data: SmbPath) = data.path.isVideo() || data.path.isImage()
    }
}
