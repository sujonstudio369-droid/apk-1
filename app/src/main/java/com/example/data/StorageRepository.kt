package com.example.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.util.Locale

class StorageRepository(private val context: Context) {

    private val disconnectedDevices = mutableSetOf<String>()

    fun setDeviceEjected(deviceId: String) {
        disconnectedDevices.add(deviceId)
    }

    fun reconnectAll() {
        disconnectedDevices.clear()
    }

    suspend fun loadDevices(): List<Device> = withContext(Dispatchers.IO) {
        val result = mutableListOf<Device>()
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager

        // 1. Local Storage (Internal)
        val extDir = Environment.getExternalStorageDirectory()
        val localStat = try {
            StatFs(extDir.path)
        } catch (_: Exception) {
            try {
                StatFs(Environment.getDataDirectory().path)
            } catch (_: Exception) {
                null
            }
        }
        val localTotal = (localStat?.blockCountLong ?: 2097152L) * (localStat?.blockSizeLong ?: 4096L)
        val localFree = (localStat?.availableBlocksLong ?: 524288L) * (localStat?.blockSizeLong ?: 4096L)
        val localUsed = maxOf(0L, localTotal - localFree)

        val localPhotos = scanAllLocalPhotos()
        val localFolders = groupPhotosIntoFolders("local", localPhotos)

        val hasRealPhotos = localFolders.isNotEmpty()
        val finalFolders = if (hasRealPhotos) localFolders else createSampleFolders("local")
        val finalUsed = if (hasRealPhotos) localUsed else (6.28 * 1024 * 1024 * 1024).toLong()
        val finalTotal = if (hasRealPhotos) localTotal else (8.0 * 1024 * 1024 * 1024).toLong()

        result.add(
            Device(
                id = "local",
                name = "Local Storage",
                type = DeviceType.INTERNAL,
                usedBytes = finalUsed,
                totalBytes = finalTotal,
                connected = true,
                folders = finalFolders
            )
        )

        // 2. Removable volumes (Pendrive / USB) - ONLY added when physically connected
        var usbIndex = 1
        val volumes = storageManager?.storageVolumes ?: emptyList()
        for (volume in volumes) {
            val isRemovable = volume.isRemovable || !volume.isPrimary
            if (!isRemovable) continue

            val state = volume.state
            val isMounted = state == Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY
            if (!isMounted) continue

            val volId = volume.uuid ?: "usb_$usbIndex"
            val volName = if (usbIndex == 1) "Pendrive" else "Pendrive $usbIndex"
            usbIndex++

            val isDisconnected = disconnectedDevices.contains(volId)
            if (isDisconnected) continue

            val dir = getVolumeDirectory(volume)
            var usbTotal = 32L * 1024 * 1024 * 1024 // 32 GB default
            var usbUsed = 8L * 1024 * 1024 * 1024
            if (dir != null && dir.exists()) {
                try {
                    val stat = StatFs(dir.path)
                    val total = stat.blockCountLong * stat.blockSizeLong
                    val free = stat.availableBlocksLong * stat.blockSizeLong
                    if (total > 0) {
                        usbTotal = total
                        usbUsed = maxOf(0L, total - free)
                    }
                } catch (_: Exception) {}
            }

            // Scan files from USB
            val usbPhotos = if (dir != null && dir.exists() && dir.canRead()) {
                scanDirectory(volId, dir)
            } else {
                emptyList()
            }
            val usbFolders = groupPhotosIntoFolders(volId, usbPhotos)

            result.add(
                Device(
                    id = volId,
                    name = volName,
                    type = DeviceType.USB,
                    usedBytes = usbUsed,
                    totalBytes = usbTotal,
                    connected = true,
                    folders = if (usbFolders.isNotEmpty()) usbFolders else createSampleFolders(volId, isUsb = true)
                )
            )
        }

        result
    }

    private fun scanAllLocalPhotos(): List<Photo> {
        val photos = scanMediaStore(null).toMutableList()
        val seenPaths = photos.mapNotNull { it.filePath }.toMutableSet()
        val seenIds = photos.map { it.id }.toMutableSet()

        val publicDirs = listOf(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        )
        for (dir in publicDirs) {
            if (dir != null && dir.exists() && dir.canRead()) {
                val dirPhotos = scanDirectory("local", dir)
                for (p in dirPhotos) {
                    val path = p.filePath
                    if (path != null && !seenPaths.contains(path) && !seenIds.contains(p.id)) {
                        seenPaths.add(path)
                        seenIds.add(p.id)
                        photos.add(p)
                    }
                }
            }
        }
        return photos
    }

    private fun getVolumeDirectory(volume: android.os.storage.StorageVolume): File? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                volume.directory
            } else {
                val getPathMethod = volume.javaClass.getMethod("getPath")
                val path = getPathMethod.invoke(volume) as? String
                if (path != null) File(path) else null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun scanMediaStore(volumeName: String?): List<Photo> {
        val photos = mutableListOf<Photo>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID
        )

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"

        try {
            context.contentResolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val bucketIdCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "photo_$id.jpg"
                    val mime = cursor.getString(mimeCol) ?: "image/jpeg"
                    val size = cursor.getLong(sizeCol)
                    val dateModified = cursor.getLong(dateCol) * 1000L
                    var width = cursor.getInt(widthCol)
                    var height = cursor.getInt(heightCol)
                    val bucketName = cursor.getString(bucketNameCol) ?: "Pictures"
                    val bucketId = cursor.getString(bucketIdCol) ?: "pictures"

                    val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    if (width <= 0 || height <= 0) {
                        val bounds = decodeBounds(contentUri)
                        width = bounds.first
                        height = bounds.second
                    }

                    photos.add(
                        Photo(
                            id = "local:$bucketId:$id",
                            uri = contentUri,
                            name = name,
                            mime = Formatters.readableMime(mime),
                            width = width,
                            height = height,
                            size = size,
                            date = dateModified,
                            deviceId = "local",
                            folderId = bucketName.lowercase().replace(" ", "_"),
                            filePath = null
                        )
                    )
                }
            }
        } catch (_: Exception) {}
        return photos
    }

    private fun scanDirectory(volId: String, dir: File): List<Photo> {
        val photos = mutableListOf<Photo>()
        val imageExtensions = setOf("jpg", "jpeg", "png", "webp", "gif", "bmp", "heic", "heif", "avif")

        fun walk(folder: File) {
            val files = folder.listFiles() ?: return
            for (f in files) {
                if (f.name.startsWith(".") || f.name.equals("Android", ignoreCase = true)) continue
                if (f.isDirectory) {
                    walk(f)
                } else if (f.isFile) {
                    val ext = f.extension.lowercase(Locale.ROOT)
                    if (imageExtensions.contains(ext)) {
                        val folderName = folder.name
                        val folderId = folderName.lowercase(Locale.ROOT).replace(" ", "_")
                        val bounds = decodeBoundsFromFile(f)
                        photos.add(
                            Photo(
                                id = "$volId:$folderId:${f.name}",
                                uri = Uri.fromFile(f),
                                name = f.name,
                                mime = Formatters.readableMime("image/$ext"),
                                width = bounds.first,
                                height = bounds.second,
                                size = f.length(),
                                date = f.lastModified(),
                                deviceId = volId,
                                folderId = folderId,
                                filePath = f.absolutePath
                            )
                        )
                    }
                }
            }
        }

        walk(dir)
        return photos
    }

    private fun decodeBounds(uri: Uri): Pair<Int, Int> {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(stream, null, opts)
                Pair(maxOf(1, opts.outWidth), maxOf(1, opts.outHeight))
            } ?: Pair(1920, 1080)
        } catch (_: Exception) {
            Pair(1920, 1080)
        }
    }

    private fun decodeBoundsFromFile(file: File): Pair<Int, Int> {
        return try {
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, opts)
            Pair(maxOf(1, opts.outWidth), maxOf(1, opts.outHeight))
        } catch (_: Exception) {
            Pair(1920, 1080)
        }
    }

    private fun groupPhotosIntoFolders(deviceId: String, photos: List<Photo>): List<Folder> {
        val groups = photos.groupBy { it.folderId }
        return groups.map { (folderId, list) ->
            val folderName = list.firstOrNull()?.let {
                val fName = it.filePath?.let { p -> File(p).parentFile?.name }
                fName ?: folderId.replace("_", " ").replaceFirstChar { c -> c.uppercase() }
            } ?: folderId
            Folder(
                id = folderId,
                name = folderName,
                deviceId = deviceId,
                photos = list
            )
        }
    }

    // High quality sample photos matching the HTML reference when no photos are on the device
    private fun createSampleFolders(deviceId: String, isUsb: Boolean = false): List<Folder> {
        val defs = if (!isUsb) {
            listOf(
                Triple("camera", "Camera", 14),
                Triple("screenshots", "Screenshots", 8),
                Triple("wallpapers", "Wallpapers", 10),
                Triple("downloads", "Downloads", 6)
            )
        } else {
            listOf(
                Triple("travel", "Travel", 16),
                Triple("nature", "Nature", 12),
                Triple("architecture", "Architecture", 9),
                Triple("backups", "Backups", 7),
                Triple("projects", "Projects", 6),
                Triple("archive", "Archive", 8)
            )
        }

        val artNames = listOf("Alpine Ridge", "Dune Light", "Moonrise", "Orbit", "Skyline", "Tide")

        return defs.map { (fid, fname, count) ->
            val photos = (0 until count).map { i ->
                val style = (i + fid.hashCode()) % artNames.size
                val absStyle = if (style < 0) -style else style
                val artName = artNames[absStyle]
                val seed = (fid.hashCode() * 31 + i * 977)
                val orientation = (seed shr 3) % 6
                val (w, h) = when (orientation) {
                    0 -> Pair(1067, 1600)
                    1 -> Pair(1400, 1400)
                    else -> Pair(1600, 1067)
                }
                val photoName = "$artName ${String.format(Locale.US, "%02d", i + 1)}.jpg"
                val photoSize = (1.1 + (Math.abs(seed) % 52) / 10.0) * 1048576.0
                val date = 1709251200000L + (Math.abs(seed) % 540) * 86400000L

                Photo(
                    id = "$deviceId-$fid-$i",
                    uri = Uri.parse("photoviewer://sample/$deviceId/$fid/$i"),
                    name = photoName,
                    mime = "JPEG image",
                    width = w,
                    height = h,
                    size = photoSize.toLong(),
                    date = date,
                    deviceId = deviceId,
                    folderId = fid,
                    kind = "art",
                    seed = seed,
                    style = absStyle
                )
            }
            Folder(
                id = fid,
                name = fname,
                deviceId = deviceId,
                photos = photos
            )
        }
    }

    suspend fun saveToDownloads(photo: Photo): Boolean = withContext(Dispatchers.IO) {
        try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, photo.name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val destUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return@withContext false

            var success = false
            context.contentResolver.openOutputStream(destUri)?.use { out ->
                if (photo.kind == "art") {
                    val bmp = SampleArtGenerator.renderArt(photo, 1.0f)
                    bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
                    success = true
                } else {
                    context.contentResolver.openInputStream(photo.uri)?.use { inp ->
                        inp.copyTo(out)
                        success = true
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(destUri, values, null, null)
            }
            success
        } catch (_: Exception) {
            false
        }
    }
}
