package com.example.state

import com.example.data.Photo

data class ViewerState(
    val open: Boolean = false,
    val list: List<Photo> = emptyList(),
    val idx: Int = 0,
    val scale: Float = 1.0f,
    val tx: Float = 0f,
    val ty: Float = 0f,
    val rotation: Float = 0f,
    val fit: Float = 1.0f,
    val naturalW: Int = 0,
    val naturalH: Int = 0,
    val isSlideshow: Boolean = false,
    val uiMode: Boolean = false,
    val showInfo: Boolean = false,
    val toolbarOpen: Boolean = true,
    val stripOpen: Boolean = true,
    val badgeText: String? = null
) {
    val currentPhoto: Photo? get() = list.getOrNull(idx)
    val isZoomed: Boolean get() = scale > 1.001f
}
