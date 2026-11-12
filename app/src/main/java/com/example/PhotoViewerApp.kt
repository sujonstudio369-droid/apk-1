package com.example

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.state.AppViewModel
import com.example.state.ModalType
import com.example.state.PopoverType
import com.example.state.Route
import com.example.ui.components.BtnIcon
import com.example.ui.components.DeviceOptionsPopover
import com.example.ui.components.DevicePropertiesModal
import com.example.ui.components.RemoteHintBar
import com.example.ui.components.ShortcutsModal
import com.example.ui.components.SortPopover
import com.example.ui.components.ToastOverlay
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MenuDrawer
import com.example.ui.screens.PermissionScreen
import com.example.ui.screens.ViewerScreen
import com.example.ui.theme.AccColor
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.BgColor
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.tvFocusRing

@Composable
fun PhotoViewerApp(
    viewModel: AppViewModel,
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current

    val currentRoute by viewModel.currentRoute.collectAsState()
    val devices by viewModel.devices.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val sortKey by viewModel.sort.collectAsState()
    val tileSizePx by viewModel.tileSize.collectAsState()
    val storagePanelOpen by viewModel.storagePanelOpen.collectAsState()
    val foldersPanelOpen by viewModel.foldersPanelOpen.collectAsState()
    val tvMode by viewModel.tvMode.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val viewerState by viewModel.viewerState.collectAsState()
    val activeModal by viewModel.activeModal.collectAsState()
    val activePopover by viewModel.activePopover.collectAsState()
    val deviceForOptions by viewModel.deviceForOptions.collectAsState()
    val isDrawerOpen by viewModel.isDrawerOpen.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    // Back handling
    BackHandler {
        viewModel.onRemoteAction(com.example.remote.RemoteAction.BACK)
    }

    val hintBarHeight = 0.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header (h=4rem, bg s1, border-bottom line)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(d.rem(4f))
                    .background(S1Color)
                    .border(BorderStroke(d.px(1f), LineColor))
                    .padding(horizontal = d.rem(1.5f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo Button
                val logoInteraction = remember { MutableInteractionSource() }
                val logoFocused by logoInteraction.collectIsFocusedAsState()
                val logoRotation by animateFloatAsState(
                    targetValue = if (logoFocused) -6f else 0f,
                    label = "logo_rot"
                )
                val logoScale by animateFloatAsState(
                    targetValue = if (logoFocused) 1.05f else 1f,
                    label = "logo_scale"
                )

                Row(
                    modifier = Modifier
                        .tvFocusRing(logoFocused, d.rem(0.8f))
                        .clip(RoundedCornerShape(d.rem(0.8f)))
                        .focusable(true, logoInteraction)
                        .clickable(
                            interactionSource = logoInteraction,
                            indication = null,
                            onClick = { viewModel.navigateTo(Route.Home) },
                            role = Role.Button
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.rem(0.75f))
                ) {
                    val logoShape = RoundedCornerShape(d.rem(0.8f))
                    Box(
                        modifier = Modifier
                            .size(d.rem(2.5f))
                            .rotate(logoRotation)
                            .scale(logoScale)
                            .clip(logoShape)
                            .background(S2Color)
                            .border(BorderStroke(d.px(1f), S4Color), logoShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(icon = AppIconType.IMAGE, tint = AccColor, size = d.rem(1.25f))
                    }

                    Text(
                        text = "Photo Viewer",
                        color = FgColor,
                        fontSize = d.sp(1.125f),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Menu Button
                BtnIcon(
                    icon = AppIconType.MENU,
                    onClick = { viewModel.toggleDrawer() },
                    contentDescription = "Open menu"
                )
            }

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (!hasPermission) {
                    PermissionScreen(onRequestPermission = onRequestPermission)
                } else {
                    when (currentRoute) {
                        is Route.Home -> {
                            HomeScreen(
                                devices = devices,
                                favoritesCount = favorites.size,
                                onDeviceClick = { dev -> viewModel.navigateTo(Route.Device(dev.id)) },
                                onDeviceOptionsClick = { dev -> viewModel.openDeviceOptions(dev) },
                                onFavoritesClick = { viewModel.navigateTo(Route.Fav) }
                            )
                        }
                        is Route.Device, is Route.Folder, is Route.Fav -> {
                            val allPhotos = viewModel.getAllPhotos()
                            val visiblePhotos = viewModel.getVisiblePhotosForCurrentRoute()
                            val visibleFolders = if (currentRoute is Route.Device) {
                                viewModel.getVisibleFoldersForDevice((currentRoute as Route.Device).deviceId)
                            } else emptyList()

                            BrowserScreen(
                                route = currentRoute,
                                devices = devices,
                                favoritesCount = favorites.size,
                                favoritesSet = favorites,
                                sortKey = sortKey,
                                tileSizePx = tileSizePx,
                                storagePanelOpen = storagePanelOpen,
                                foldersPanelOpen = foldersPanelOpen,
                                searchQuery = searchQuery,
                                visibleFolders = visibleFolders,
                                visiblePhotos = visiblePhotos,
                                allPhotos = allPhotos,
                                onToggleStoragePanel = { viewModel.toggleStoragePanel() },
                                onToggleFoldersPanel = { viewModel.toggleFoldersPanel() },
                                onNavigate = { viewModel.navigateTo(it) },
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onSortClick = { viewModel.openSortPopover() },
                                onTileSizeChange = { viewModel.setTileSize(it) },
                                onPhotoClick = { photo -> viewModel.openViewer(photo, visiblePhotos) },
                                onPhotoFavClick = { photo -> viewModel.toggleFavorite(photo.id) }
                            )
                        }
                    }
                }
            }
        }

        // Fullscreen Viewer Overlay
        if (viewerState.open) {
            val curPhoto = viewerState.currentPhoto
            ViewerScreen(
                state = viewerState,
                isFavorite = curPhoto != null && favorites.contains(curPhoto.id),
                onClose = { viewModel.closeViewer() },
                onPrev = { viewModel.prevPhoto() },
                onNext = { viewModel.nextPhoto() },
                onZoomIn = { viewModel.zoomTo(viewerState.scale * 1.4f) },
                onZoomOut = { viewModel.zoomTo(viewerState.scale / 1.4f) },
                onPanUp = { viewModel.pan(0f, 1080f * 0.25f) },
                onPanDown = { viewModel.pan(0f, -1080f * 0.25f) },
                onRotate = { viewModel.rotate() },
                onResetZoom = { viewModel.resetView() },
                onToggleToolbar = { viewModel.toggleToolbar() },
                onToggleStrip = { viewModel.toggleStrip() },
                onToggleDetails = { viewModel.toggleDetails() },
                onOpenMenu = { viewModel.openDrawer() },
                onSelectThumb = { idx -> viewModel.showPhotoAtIndex(idx) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = hintBarHeight)
            )
        }

        // Popovers
        when (activePopover) {
            PopoverType.SORT -> {
                SortPopover(
                    currentSort = sortKey,
                    onSortSelected = { viewModel.setSort(it) },
                    onDismiss = { viewModel.closePopover() }
                )
            }
            PopoverType.DEVICE_OPTIONS -> {
                deviceForOptions?.let { dev ->
                    DeviceOptionsPopover(
                        device = dev,
                        onOpen = {
                            viewModel.closePopover()
                            viewModel.navigateTo(Route.Device(dev.id))
                        },
                        onProperties = {
                            viewModel.closePopover()
                            viewModel.openModal(ModalType.DEVICE_PROPERTIES)
                        },
                        onEject = { viewModel.safelyEjectDevice(dev) },
                        onScan = { viewModel.scanDevices() },
                        onDismiss = { viewModel.closePopover() }
                    )
                }
            }
            PopoverType.NONE -> {}
        }

        // Modals
        when (activeModal) {
            ModalType.DEVICE_PROPERTIES -> {
                deviceForOptions?.let { dev ->
                    DevicePropertiesModal(
                        device = dev,
                        onClose = { viewModel.closeModal() }
                    )
                }
            }
            ModalType.SHORTCUTS -> {
                ShortcutsModal(onClose = { viewModel.closeModal() })
            }
            ModalType.NONE -> {}
        }

        // Menu Drawer
        MenuDrawer(
            isOpen = isDrawerOpen,
            currentRoute = currentRoute,
            devices = devices,
            favoritesCount = favorites.size,
            tvMode = tvMode,
            isViewerOpen = viewerState.open,
            currentPhoto = viewerState.currentPhoto,
            isCurrentPhotoFav = viewerState.currentPhoto?.let { favorites.contains(it.id) } ?: false,
            isSlideshow = viewerState.isSlideshow,
            viewerState = viewerState,
            onClose = { viewModel.closeDrawer() },
            onNavigate = { viewModel.navigateTo(it) },
            onPhotoDetails = { viewModel.toggleDetails() },
            onPhotoFavorite = { viewerState.currentPhoto?.let { viewModel.toggleFavorite(it.id) } },
            onDownload = { viewModel.downloadCurrentPhoto() },
            onToggleSlideshow = { viewModel.toggleSlideshow() },
            onToggleFullScreen = { /* Handled at Activity level */ },
            onOpenShortcuts = { viewModel.openModal(ModalType.SHORTCUTS) },
            onToggleTvMode = { viewModel.toggleTvMode() }
        )

        // Toast Overlay
        ToastOverlay(
            message = toastMessage,
            bottomPadding = d.rem(1.5f)
        )
    }
}
