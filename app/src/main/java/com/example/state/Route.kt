package com.example.state

sealed class Route {
    object Home : Route()
    object Fav : Route()
    data class Device(val deviceId: String) : Route()
    data class Folder(val deviceId: String, val folderId: String) : Route()
}
