package com.example.data

import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import android.net.Uri

class SampleImageFetcher(
    private val data: Uri,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        // Parse uri: photoviewer://sample/{deviceId}/{folderId}/{index}
        val segments = data.pathSegments
        val devId = if (segments.isNotEmpty()) segments[0] else "local"
        val folderId = if (segments.size > 1) segments[1] else "camera"
        val idx = if (segments.size > 2) segments[2].toIntOrNull() ?: 0 else 0

        val seed = folderId.hashCode() * 31 + idx * 977
        val orientation = (seed shr 3) % 6
        val (w, h) = when (orientation) {
            0 -> Pair(1067, 1600)
            1 -> Pair(1400, 1400)
            else -> Pair(1600, 1067)
        }
        val style = Math.abs((idx + folderId.hashCode()) % 6)
        val photo = Photo(
            id = "$devId-$folderId-$idx",
            uri = data,
            name = "sample.jpg",
            mime = "JPEG image",
            width = w,
            height = h,
            size = 2000000L,
            date = 1709251200000L,
            deviceId = devId,
            folderId = folderId,
            kind = "art",
            seed = seed,
            style = style
        )

        // Scale down for thumbnails if needed
        val maxDim = maxOf(options.size.width.toPx(options.scale), options.size.height.toPx(options.scale))
        val scale = if (maxDim > 0 && maxDim < 1920) {
            (maxDim.toFloat() / maxOf(w, h).toFloat()).coerceIn(0.15f, 1.0f)
        } else {
            1.0f
        }

        val bitmap = SampleArtGenerator.renderArt(photo, scale)
        val drawable = BitmapDrawable(options.context.resources, bitmap)
        return DrawableResult(
            drawable = drawable,
            isSampled = scale < 1.0f,
            dataSource = DataSource.MEMORY
        )
    }

    private fun coil.size.Dimension.toPx(scale: coil.size.Scale): Int {
        return when (this) {
            is coil.size.Dimension.Pixels -> px
            else -> 0
        }
    }

    class Factory : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
            if (data.scheme == "photoviewer") {
                return SampleImageFetcher(data, options)
            }
            return null
        }
    }
}
