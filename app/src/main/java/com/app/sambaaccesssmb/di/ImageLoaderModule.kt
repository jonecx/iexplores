package com.app.sambaaccesssmb.di

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import android.util.Size
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
import com.app.sambaaccesssmb.SMBAccess
import com.app.sambaaccesssmb.ui.SmbPath
import com.app.sambaaccesssmb.utils.Build
import com.app.sambaaccesssmb.utils.DirUtil
import com.app.sambaaccesssmb.utils.getFormattedName
import com.app.sambaaccesssmb.utils.parseSmbPathFromSharePath
import com.hierynomus.msdtyp.AccessMask
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2ShareAccess
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import okio.buffer
import okio.source
import java.io.FileOutputStream
import java.io.InputStream
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
            .build()
    }
}

class SmbFileFetcher(
    private val data: SmbPath,
    private val options: Options,
) : Fetcher {

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult {

        val smbFile2bRead = SMBAccess.getSmbSession().currentDiskShare.openFile(
            data.string.parseSmbPathFromSharePath(),
            EnumSet.of(AccessMask.GENERIC_READ),
            null,
            setOf(SMB2ShareAccess.FILE_SHARE_READ),
            SMB2CreateDisposition.FILE_OPEN,
            null,
        )

        val inputStream = smbFile2bRead.inputStream

        val tempFileName = data.string.getFormattedName()
/*        return DrawableResult(
            drawable = BitmapDrawable(options.context.resources, getThumbnail(tempFileName, inputStream)),
            isSampled = false,
            dataSource = MEMORY,
        )*/
        return SourceResult(
            source = ImageSource(
                source = inputStream.source().buffer(),
                context = options.context,
                metadata = ContentMetadata(Uri.parse(smbFile2bRead.uncPath)),
            ),
            mimeType = null, // Let Coil handle the MIME type
            dataSource = DISK,
        )

/*        return if (data.isVideo()) {
            val thumbnailBitmap = getThumbnail(tempFileName, inputStream)
            return DrawableResult(
                drawable = BitmapDrawable(options.context.resources, thumbnailBitmap),
                isSampled = false,
                dataSource = MEMORY,
            )
        } else {
            SourceResult(
                source = ImageSource(
                    source = inputStream.source().buffer(),
                    context = options.context,
                    metadata = ContentMetadata(Uri.parse(data.string)),
                ),
                mimeType = null, // Let Coil handle the MIME type
                dataSource = DISK,
            )
        }*/
    }

    private fun getThumbnail(fileName: String, inputStream: InputStream): Bitmap? {
        runCatching {
            // TODO CoilImage smb client video thumbnail support (check if temp file is around: reuse)
            val tempFile = DirUtil.getTempFile(fileName)!!

            if (!tempFile.exists()) {
                val outputStream = FileOutputStream(tempFile)
                val buffer = ByteArray(16 * 1024)
                var bytesRead = inputStream.read(buffer)

                // Read the first 20 seconds of the video and write to temporary file
                var totalBytesRead = 0
                while (bytesRead != -1 && totalBytesRead < 5000000) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalBytesRead += bytesRead
                    bytesRead = inputStream.read(buffer)
                }
            }

            return ThumbnailUtils.createImageThumbnail(tempFile, Size(500, 500), null)
        }.getOrElse {
            print(it.localizedMessage)
            return null
        }

    }

    class Factory : Fetcher.Factory<SmbPath> {

        override fun create(data: SmbPath, options: Options, imageLoader: ImageLoader): Fetcher? {
            if (!isApplicable(data)) return null
            return SmbFileFetcher(data, options)
        }

        private fun isApplicable(data: SmbPath) = data.string.isNotEmpty()// != null
    }
}
