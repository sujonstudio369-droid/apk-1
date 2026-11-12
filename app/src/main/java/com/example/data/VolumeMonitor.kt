package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager

class VolumeMonitor(
    private val context: Context,
    private val onDeviceMounted: (name: String) -> Unit,
    private val onDeviceUnmounted: (name: String) -> Unit
) {
    private var isRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            when (action) {
                IntentFilter("android.intent.action.MEDIA_MOUNTED").toString(),
                Intent.ACTION_MEDIA_MOUNTED,
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    onDeviceMounted("Pendrive")
                }
                Intent.ACTION_MEDIA_UNMOUNTED,
                Intent.ACTION_MEDIA_REMOVED,
                Intent.ACTION_MEDIA_BAD_REMOVAL,
                Intent.ACTION_MEDIA_EJECT,
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    onDeviceUnmounted("Pendrive")
                }
            }
        }
    }

    fun start() {
        if (isRegistered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_MEDIA_MOUNTED)
            addAction(Intent.ACTION_MEDIA_UNMOUNTED)
            addAction(Intent.ACTION_MEDIA_REMOVED)
            addAction(Intent.ACTION_MEDIA_BAD_REMOVAL)
            addAction(Intent.ACTION_MEDIA_EJECT)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addDataScheme("file")
        }
        try {
            context.registerReceiver(receiver, filter)
            isRegistered = true
        } catch (_: Exception) {}
    }

    fun stop() {
        if (!isRegistered) return
        try {
            context.unregisterReceiver(receiver)
            isRegistered = false
        } catch (_: Exception) {}
    }
}
