package com.example.data

import android.net.Uri

enum class DeviceType {
    INTERNAL,
    USB
}

data class Photo(
    val id: String,
    val uri: Uri,
    val name: String,
    val mime: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val date: Long,
    val deviceId: String,
    val folderId: String,
    val filePath: String? = null,
    val kind: String = "file",
    val seed: Int = 0,
    val style: Int = 0
)

data class Folder(
    val id: String,
    val name: String,
    val deviceId: String,
    val photos: List<Photo> = emptyList()
) {
    val date: Long get() = photos.maxOfOrNull { it.date } ?: 0L
    val size: Long get() = photos.sumOf { it.size }
}

data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val usedBytes: Long,
    val totalBytes: Long,
    val connected: Boolean = true,
    val folders: List<Folder> = emptyList()
) {
    val photoCount: Int get() = folders.sumOf { it.photos.size }
}
