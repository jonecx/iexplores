package com.app.sambaaccesssmb.di

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import android.util.Size
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
import com.app.sambaaccesssmb.utils.Build
import com.app.sambaaccesssmb.utils.DirUtil
import com.app.sambaaccesssmb.utils.getFormattedName
import com.app.sambaaccesssmb.utils.isVideo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import okhttp3.internal.closeQuietly
import okio.buffer
import okio.source
import org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_ARGB
import org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_RGBA
import org.bytedeco.javacv.AndroidFrameConverter
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.FrameGrabber
import timber.log.Timber
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception

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
    private val data: SmbFile,
    private val options: Options,
) : Fetcher {

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun fetch(): FetchResult {
        val inputStream = data.inputStream

        val tempFileName = data.getFormattedName()
        return if (data.isVideo()) {
/*            try {
                val frameGrabber = FFmpegFrameGrabber(inputStream)
                frameGrabber.pixelFormat = AV_PIX_FMT_RGBA
                frameGrabber.start()
                val frame = frameGrabber.grabFrame()
                val xy = AndroidFrameConverter()
                val x = xy.convert(frame)
                print(x)
            } catch (e: Exception) {
                print(e.stackTrace)
            }*/

//            val thumbnailBitmap = getThumbnail(tempFileName, inputStream)
            val thumbnailBitmap = try {
                val frameGrabber = FFmpegFrameGrabber(inputStream)
//                frameGrabber.pixelFormat = AV_PIX_FMT_ARGB
                frameGrabber.frameNumber = 1
                frameGrabber.setOption("stimeout", "60000000")
                frameGrabber.setOption("hwaccel", "cuvid")
                frameGrabber.setOption("hwaccel", "h264_videotoolbox")
                frameGrabber.start()
                var image: Frame? = null
                var counter = 0
                while(image == null) {
                    counter++
                    image = frameGrabber.grabImage()
                    Timber.d("Jonecx $counter")
                }
                val xy = AndroidFrameConverter()
                val x = xy.convert(image)
                frameGrabber.stop()
                x ?: ContextCompat.getDrawable(options.context, R.drawable.ic_file)!!.toBitmapOrNull()
            } catch (e: Exception) {
                print(e.stackTrace)
//                getThumbnail(tempFileName, inputStream)
                ContextCompat.getDrawable(options.context, R.drawable.ic_folder)!!.toBitmapOrNull()
            } finally {
                inputStream.closeQuietly()
            }


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
                    metadata = ContentMetadata(Uri.parse(data.path)),
                ),
                mimeType = null, // Let Coil handle the MIME type
                dataSource = DISK,
            )
        }
    }

    private fun getThumbnail(fileName: String, inputStream: InputStream): Bitmap {
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
        return ThumbnailUtils.createVideoThumbnail(tempFile, Size(500, 500), null)
    }

    class Factory : Fetcher.Factory<SmbFile> {

        override fun create(data: SmbFile, options: Options, imageLoader: ImageLoader): Fetcher? {
            if (!isApplicable(data)) return null
            return SmbFileFetcher(data, options)
        }

        private fun isApplicable(data: SmbFile) = data.context != null
    }
}
