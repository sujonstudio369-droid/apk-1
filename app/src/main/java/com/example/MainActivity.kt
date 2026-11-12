package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import coil.Coil
import coil.ImageLoader
import com.example.data.SampleImageFetcher
import com.example.remote.KeyMapper
import com.example.state.AppViewModel
import com.example.ui.theme.PhotoViewerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()
    private var hasStoragePermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup Coil with sample procedural image fetcher
        val imageLoader = ImageLoader.Builder(this)
            .components {
                add(SampleImageFetcher.Factory())
            }
            .crossfade(true)
            .build()
        Coil.setImageLoader(imageLoader)

        checkPermissionState()

        setContent {
            val tvMode by viewModel.tvMode.collectAsState()
            val viewerState by viewModel.viewerState.collectAsState()

            // Keep screen on when viewer is open or slideshow is active
            if (viewerState.open) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val granted = permissions.values.any { it }
                hasStoragePermission = granted
                if (granted) {
                    viewModel.refreshDevices()
                }
            }

            PhotoViewerTheme(tvMode = tvMode) {
                PhotoViewerApp(
                    viewModel = viewModel,
                    hasPermission = hasStoragePermission,
                    onRequestPermission = {
                        val needed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                        } else {
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        permissionLauncher.launch(needed)
                    }
                )
            }
        }
    }

    private fun checkPermissionState() {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        hasStoragePermission = ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            val action = KeyMapper.mapKeyEvent(event, isTyping = false)
            if (action != null) {
                if (event.repeatCount > 0 && !KeyMapper.isRepeatAllowed(action)) {
                    return true
                }
                val handled = viewModel.onRemoteAction(action)
                if (handled) return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
}
