package com.example.ui.screens

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.Device
import com.example.data.Folder
import com.example.data.Photo
import com.example.state.Route
import com.example.ui.components.BtnIcon
import com.example.ui.components.FolderCard
import com.example.ui.components.FoldersPanel
import com.example.ui.components.PhotoCard
import com.example.ui.components.SearchField
import com.example.ui.components.StoragePanel
import com.example.ui.theme.AccColor
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.BgColor
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.MuteColor
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor
import com.example.ui.theme.tvFocusRing

@Composable
fun BrowserScreen(
    route: Route,
    devices: List<Device>,
    favoritesCount: Int,
    favoritesSet: Set<String>,
    sortKey: String,
    tileSizePx: Int,
    storagePanelOpen: Boolean,
    foldersPanelOpen: Boolean,
    searchQuery: String,
    visibleFolders: List<Folder>,
    visiblePhotos: List<Photo>,
    allPhotos: List<Photo>,
    onToggleStoragePanel: () -> Unit,
    onToggleFoldersPanel: () -> Unit,
    onNavigate: (Route) -> Unit,
    onSearchChange: (String) -> Unit,
    onSortClick: () -> Unit,
    onTileSizeChange: (Int) -> Unit,
    onPhotoClick: (Photo) -> Unit,
    onPhotoFavClick: (Photo) -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current

    val currentDeviceId = when (route) {
        is Route.Device -> route.deviceId
        is Route.Folder -> route.deviceId
        else -> null
    }
    val currentDevice = devices.find { it.id == currentDeviceId }
    val currentFolderId = (route as? Route.Folder)?.folderId
    val currentFolder = currentDevice?.folders?.find { it.id == currentFolderId }

    val isFolderView = route is Route.Folder
    val isDeviceView = route is Route.Device
    val isFavView = route is Route.Fav

    val sortLabel = when (sortKey) {
        "name-desc" -> "Name Z–A"
        "new" -> "Newest"
        "old" -> "Oldest"
        "big" -> "Largest"
        "small" -> "Smallest"
        else -> "Name A–Z"
    }

    Row(modifier = modifier.fillMaxSize()) {
        // 1. Storage panel
        StoragePanel(
            devices = devices,
            favoriteCount = favoritesCount,
            currentDeviceId = currentDeviceId,
            isFavRoute = isFavView,
            isOpen = storagePanelOpen,
            onToggle = onToggleStoragePanel,
            onDeviceClick = { onNavigate(Route.Device(it.id)) },
            onFavClick = { onNavigate(Route.Fav) }
        )

        // 2. Folders panel (only visible inside a folder)
        if (isFolderView && currentDevice != null) {
            FoldersPanel(
                deviceName = currentDevice.name,
                folders = currentDevice.folders,
                currentFolderId = currentFolderId,
                isOpen = foldersPanelOpen,
                onToggle = onToggleFoldersPanel,
                onFolderClick = { onNavigate(Route.Folder(currentDevice.id, it.id)) }
            )
        }

        // 3. Main content column
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgColor)
                    .border(BorderStroke(d.px(1f), LineColor))
                    .padding(horizontal = d.rem(1.25f), vertical = d.rem(0.6f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.rem(0.75f))
            ) {
                // Breadcrumbs (takes natural content width)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.rem(0.25f)),
                    modifier = Modifier.widthIn(max = d.rem(20f))
                ) {
                    BtnIcon(
                        icon = AppIconType.HOME,
                        onClick = { onNavigate(Route.Home) },
                        size = d.rem(2f),
                        iconSize = d.rem(1f),
                        contentDescription = "Home"
                    )

                    AppIcon(
                        icon = AppIconType.CHEVRON_RIGHT,
                        tint = MuteColor,
                        size = d.rem(1f)
                    )

                    if (isFavView) {
                        Text(
                            text = "Favorites",
                            color = FgColor,
                            fontSize = d.sp(0.875f),
                            fontWeight = FontWeight.Medium
                        )
                    } else if (currentDevice != null) {
                        if (isFolderView) {
                            CrumbButton(
                                text = currentDevice.name,
                                onClick = { onNavigate(Route.Device(currentDevice.id)) }
                            )
                            AppIcon(
                                icon = AppIconType.CHEVRON_RIGHT,
                                tint = MuteColor,
                                size = d.rem(1f)
                            )
                            Text(
                                text = currentFolder?.name ?: "",
                                color = FgColor,
                                fontSize = d.sp(0.875f),
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else {
                            Text(
                                text = currentDevice.name,
                                color = FgColor,
                                fontSize = d.sp(0.875f),
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Search field
                SearchField(
                    query = searchQuery,
                    onQueryChange = onSearchChange,
                    allPhotos = allPhotos,
                    onPhotoSelected = onPhotoClick
                )

                // Sort Button
                SortButton(
                    currentLabel = sortLabel,
                    onClick = onSortClick
                )

                // Tile size switch
                SegmentedTileSwitch(
                    currentSize = tileSizePx,
                    onSizeChange = onTileSizeChange
                )
            }

            // Grid scroll area
            val computedTile = when (tileSizePx) {
                130 -> d.rem(4.5f)   // Small tiles: ~65-75 dp, dense multi-column grid
                250 -> d.rem(8.5f)   // Large tiles: ~125-140 dp
                else -> d.rem(6.25f) // Medium tiles: ~90-105 dp
            }
            val tileGap = if (tileSizePx == 130) d.rem(0.6f) else d.rem(1f)
            val gridPadding = if (tileSizePx == 130) d.rem(1f) else d.rem(1.5f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(gridPadding)
            ) {
                if (isDeviceView) {
                    if (visibleFolders.isEmpty()) {
                        EmptyState(
                            isSearch = searchQuery.isNotEmpty(),
                            query = searchQuery,
                            defaultTitle = "No folders here",
                            defaultHint = "Nothing to show here yet."
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = computedTile),
                            horizontalArrangement = Arrangement.spacedBy(tileGap),
                            verticalArrangement = Arrangement.spacedBy(tileGap),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(visibleFolders, key = { it.id }) { folder ->
                                FolderCard(
                                    folder = folder,
                                    minTileSize = computedTile,
                                    onClick = { onNavigate(Route.Folder(folder.deviceId, folder.id)) }
                                )
                            }
                        }
                    }
                } else {
                    if (visiblePhotos.isEmpty()) {
                        val defaultTitle = if (isFavView) "No favorites yet" else "This folder is empty"
                        val defaultHint = if (isFavView) "Tap the heart on any photo to keep it here." else "Nothing to show here yet."
                        EmptyState(
                            isSearch = searchQuery.isNotEmpty(),
                            query = searchQuery,
                            isFavorites = isFavView,
                            defaultTitle = defaultTitle,
                            defaultHint = defaultHint
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = computedTile),
                            horizontalArrangement = Arrangement.spacedBy(tileGap),
                            verticalArrangement = Arrangement.spacedBy(tileGap),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(visiblePhotos, key = { it.id }) { photo ->
                                PhotoCard(
                                    photo = photo,
                                    isFavorite = favoritesSet.contains(photo.id),
                                    onClick = { onPhotoClick(photo) },
                                    onFavClick = { onPhotoFavClick(photo) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrumbButton(
    text: String,
    onClick: () -> Unit
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(d.rem(0.5f))
    val bgColor = if (isFocused) S2Color else Color.Transparent

    Box(
        modifier = Modifier
            .tvFocusRing(isFocused, d.rem(0.5f))
            .clip(shape)
            .background(bgColor)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(horizontal = d.rem(0.5f), vertical = d.rem(0.25f))
    ) {
        Text(
            text = text,
            color = if (isFocused) FgColor else SubColor,
            fontSize = d.sp(0.875f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SortButton(
    currentLabel: String,
    onClick: () -> Unit
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(d.rem(0.9f))
    val bgColor = if (isFocused) S2Color else S1Color
    val borderColor = if (isFocused) MuteColor else LineColor

    Row(
        modifier = Modifier
            .height(d.rem(2.5f))
            .tvFocusRing(isFocused, d.rem(0.9f))
            .clip(shape)
            .background(bgColor)
            .border(BorderStroke(d.px(1f), borderColor), shape)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(horizontal = d.rem(0.8f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.rem(0.5f))
    ) {
        AppIcon(icon = AppIconType.SORT, tint = FgColor, size = d.rem(1.05f))
        Text(text = currentLabel, color = FgColor, fontSize = d.sp(0.875f))
        AppIcon(icon = AppIconType.CHEVRON_DOWN, tint = MuteColor, size = d.rem(1f))
    }
}

@Composable
fun SegmentedTileSwitch(
    currentSize: Int,
    onSizeChange: (Int) -> Unit
) {
    val d = LocalDimens.current
    val shape = RoundedCornerShape(d.rem(0.9f))
    val sizes = listOf(Pair(130, "S"), Pair(180, "M"), Pair(250, "L"))

    Row(
        modifier = Modifier
            .clip(shape)
            .background(S1Color)
            .border(BorderStroke(d.px(1f), LineColor), shape)
            .padding(d.rem(0.2f)),
        horizontalArrangement = Arrangement.spacedBy(d.rem(0.15f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        sizes.forEach { (sz, label) ->
            val isSelected = currentSize == sz
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()

            val btnShape = RoundedCornerShape(d.rem(0.65f))
            val btnBg = when {
                isSelected -> S4Color
                isFocused -> S2Color
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .size(width = d.rem(2.1f), height = d.rem(2f))
                    .tvFocusRing(isFocused, d.rem(0.65f))
                    .clip(btnShape)
                    .background(btnBg)
                    .focusable(true, interactionSource)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { onSizeChange(sz) },
                        role = Role.Button
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected || isFocused) FgColor else MuteColor,
                    fontSize = d.sp(0.75f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    isSearch: Boolean,
    query: String = "",
    isFavorites: Boolean = false,
    defaultTitle: String,
    defaultHint: String
) {
    val d = LocalDimens.current

    val title = if (isSearch) "No results for “$query”" else defaultTitle
    val hint = if (isSearch) "Try a different search term." else defaultHint
    val icon = if (isFavorites && !isSearch) AppIconType.HEART else AppIconType.IMAGE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = d.rem(6f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val iconShape = RoundedCornerShape(d.rem(1f))
        Box(
            modifier = Modifier
                .size(d.rem(4f))
                .clip(iconShape)
                .background(S2Color)
                .border(BorderStroke(d.px(1f), LineColor), iconShape)
                .padding(bottom = d.rem(1f)),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(icon = icon, tint = AccColor, size = d.rem(1.75f))
        }

        Spacer(modifier = Modifier.height(d.rem(1f)))

        Text(
            text = title,
            color = FgColor,
            fontSize = d.sp(1f),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(d.rem(0.25f)))

        Text(
            text = hint,
            color = MuteColor,
            fontSize = d.sp(0.875f)
        )
    }
}
