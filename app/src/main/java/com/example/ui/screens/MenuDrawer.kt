package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.DeviceType
import com.example.data.Formatters
import com.example.data.Photo
import com.example.state.Route
import com.example.state.ViewerState
import com.example.ui.components.BtnIcon
import com.example.ui.components.HintItem
import com.example.ui.theme.AccColor
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.ColorBlue
import com.example.ui.theme.ColorGreen
import com.example.ui.theme.ColorRed
import com.example.ui.theme.ColorYellow
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.MuteColor
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.ScrimColor
import com.example.ui.theme.SubColor
import com.example.ui.theme.tvFocusRing

@Composable
fun MenuDrawer(
    isOpen: Boolean,
    currentRoute: Route,
    devices: List<Device>,
    favoritesCount: Int,
    tvMode: Boolean,
    isViewerOpen: Boolean,
    currentPhoto: Photo?,
    isCurrentPhotoFav: Boolean,
    isSlideshow: Boolean,
    viewerState: ViewerState = ViewerState(),
    onClose: () -> Unit,
    onNavigate: (Route) -> Unit,
    onPhotoDetails: () -> Unit,
    onPhotoFavorite: () -> Unit,
    onDownload: () -> Unit,
    onToggleSlideshow: () -> Unit,
    onToggleFullScreen: () -> Unit,
    onOpenShortcuts: () -> Unit,
    onToggleTvMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current

    if (isOpen) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ScrimColor)
                .clickable(onClick = onClose)
        ) {
            AnimatedVisibility(
                visible = isOpen,
                enter = slideInHorizontally { it } + fadeIn(),
                exit = slideOutHorizontally { it } + fadeOut(),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(min = d.rem(20f), max = d.rem(22f))
                        .fillMaxHeight()
                        .clickable(enabled = false) {}
                        .background(S1Color)
                        .border(BorderStroke(d.px(1f), LineColor))
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(d.rem(4f))
                            .border(BorderStroke(d.px(1f), LineColor))
                            .padding(horizontal = d.rem(1.25f)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Menu",
                            color = FgColor,
                            fontSize = d.sp(1.125f),
                            fontWeight = FontWeight.SemiBold
                        )
                        BtnIcon(
                            icon = AppIconType.X,
                            onClick = onClose,
                            contentDescription = "Close menu"
                        )
                    }

                    // Body
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = d.rem(1.5f))
                    ) {
                        // Section: Photo (only when viewer is open)
                        if (isViewerOpen && currentPhoto != null) {
                            SectionHeader(title = "PHOTO")
                            MenuItem(
                                icon = AppIconType.INFO,
                                label = "Photo details",
                                kbdHint = "I",
                                onClick = onPhotoDetails
                            )
                            MenuItem(
                                icon = AppIconType.HEART,
                                label = if (isCurrentPhotoFav) "Remove from favorites" else "Add to favorites",
                                kbdHint = "H",
                                onClick = onPhotoFavorite
                            )
                            MenuItem(
                                icon = AppIconType.DOWNLOAD,
                                label = "Download",
                                onClick = onDownload
                            )
                            MenuItem(
                                icon = if (isSlideshow) AppIconType.PAUSE else AppIconType.PLAY,
                                label = if (isSlideshow) "Stop slideshow" else "Start slideshow",
                                kbdHint = "Space",
                                onClick = onToggleSlideshow
                            )
                            MenuItem(
                                icon = AppIconType.MAXIMIZE,
                                label = "Full screen",
                                kbdHint = "F",
                                onClick = onToggleFullScreen
                            )
                        }

                        // Section: Browse
                        SectionHeader(title = "BROWSE")
                        MenuItem(
                            icon = AppIconType.HOME,
                            label = "Home",
                            isCurrent = currentRoute is Route.Home && !isViewerOpen,
                            onClick = {
                                onClose()
                                onNavigate(Route.Home)
                            }
                        )

                        devices.filter { it.connected }.forEach { dev ->
                            val isCurrent = (currentRoute as? Route.Device)?.deviceId == dev.id
                            MenuItem(
                                icon = if (dev.type == DeviceType.USB) AppIconType.USB else AppIconType.SD,
                                label = dev.name,
                                trailingText = Formatters.fmtGB(dev.usedBytes),
                                isCurrent = isCurrent && !isViewerOpen,
                                onClick = {
                                    onClose()
                                    onNavigate(Route.Device(dev.id))
                                }
                            )
                        }

                        MenuItem(
                            icon = AppIconType.HEART,
                            label = "Favorites",
                            badgeCount = favoritesCount,
                            isCurrent = currentRoute is Route.Fav && !isViewerOpen,
                            onClick = {
                                onClose()
                                onNavigate(Route.Fav)
                            }
                        )

                        // Section: Active Remote Controls (Transferred from Footer)
                        val currentHints = when {
                            isViewerOpen -> when {
                                viewerState.showInfo -> listOf(
                                    HintItem.KeyHint("OK", "Close info"),
                                    HintItem.KeyHint("BACK", "Close info")
                                )
                                viewerState.uiMode -> listOf(
                                    HintItem.KeyHint("◀ ▲ ▼ ▶", "Move in controls"),
                                    HintItem.KeyHint("OK", "Select button"),
                                    HintItem.KeyHint("BACK", "Back to photo")
                                )
                                viewerState.isZoomed -> listOf(
                                    HintItem.KeyHint("◀ ▲ ▼ ▶", "Pan image"),
                                    HintItem.KeyHint("OK", "Reset zoom (100%)"),
                                    HintItem.KeyHint("CH±", "Next / Prev photo"),
                                    HintItem.ColorHint(ColorRed, "Favorite"),
                                    HintItem.ColorHint(ColorYellow, "Rotate"),
                                    HintItem.KeyHint("BACK", "Reset zoom")
                                )
                                else -> listOf(
                                    HintItem.KeyHint("◀ ▶", "Next / Prev photo"),
                                    HintItem.KeyHint("OK", "Zoom in"),
                                    HintItem.KeyHint("▲", "Focus top controls"),
                                    HintItem.KeyHint("▼", "Filmstrip"),
                                    HintItem.ColorHint(ColorRed, "Favorite"),
                                    HintItem.ColorHint(ColorGreen, "Slideshow"),
                                    HintItem.ColorHint(ColorYellow, "Rotate 90°"),
                                    HintItem.ColorHint(ColorBlue, "Details & EXIF"),
                                    HintItem.KeyHint("BACK", "Close viewer")
                                )
                            }
                            currentRoute is Route.Home -> listOf(
                                HintItem.KeyHint("◀ ▲ ▼ ▶", "Navigate devices"),
                                HintItem.KeyHint("OK", "Open storage"),
                                HintItem.KeyHint("INFO", "Device options"),
                                HintItem.KeyHint("MENU", "Close menu")
                            )
                            currentRoute is Route.Device -> listOf(
                                HintItem.KeyHint("◀ ▲ ▼ ▶", "Navigate folders"),
                                HintItem.KeyHint("OK", "Open folder"),
                                HintItem.ColorHint(ColorGreen, "Sort folders"),
                                HintItem.ColorHint(ColorYellow, "Tile size (S/M/L)"),
                                HintItem.ColorHint(ColorBlue, "Search"),
                                HintItem.KeyHint("CH±", "Page up / down"),
                                HintItem.KeyHint("BACK", "Back to Home")
                            )
                            else -> listOf(
                                HintItem.KeyHint("◀ ▲ ▼ ▶", "Navigate photos"),
                                HintItem.KeyHint("OK", "View full photo"),
                                HintItem.ColorHint(ColorRed, "Favorite"),
                                HintItem.ColorHint(ColorGreen, "Sort photos"),
                                HintItem.ColorHint(ColorYellow, "Tile size (S/M/L)"),
                                HintItem.ColorHint(ColorBlue, "Search"),
                                HintItem.KeyHint("CH±", "Page up / down"),
                                HintItem.KeyHint("BACK", "Back to folders")
                            )
                        }

                        SectionHeader(title = "REMOTE CONTROLS")
                        val cardShape = RoundedCornerShape(d.rem(0.9f))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = d.rem(0.75f), vertical = d.rem(0.2f))
                                .clip(cardShape)
                                .background(S2Color)
                                .border(BorderStroke(d.px(1f), LineColor), cardShape)
                                .padding(horizontal = d.rem(0.85f), vertical = d.rem(0.75f)),
                            verticalArrangement = Arrangement.spacedBy(d.rem(0.45f))
                        ) {
                            currentHints.forEach { hint ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(d.rem(0.6f))
                                ) {
                                    when (hint) {
                                        is HintItem.KeyHint -> {
                                            val kbdShape = RoundedCornerShape(d.rem(0.35f))
                                            Box(
                                                modifier = Modifier
                                                    .clip(kbdShape)
                                                    .background(S3Color)
                                                    .border(BorderStroke(d.px(1f), S4Color), kbdShape)
                                                    .padding(horizontal = d.rem(0.4f), vertical = d.rem(0.12f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = hint.key,
                                                    color = FgColor,
                                                    fontSize = d.sp(0.66f),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        is HintItem.ColorHint -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(d.rem(0.9f))
                                                    .clip(RoundedCornerShape(d.rem(0.25f)))
                                                    .background(hint.color)
                                            )
                                        }
                                    }
                                    Text(
                                        text = when (hint) {
                                            is HintItem.KeyHint -> hint.label
                                            is HintItem.ColorHint -> hint.label
                                        },
                                        color = SubColor,
                                        fontSize = d.sp(0.78f)
                                    )
                                }
                            }
                        }

                        // Section: Help
                        SectionHeader(title = "HELP")
                        MenuItem(
                            icon = AppIconType.KEYBOARD,
                            label = "All remote shortcuts",
                            trailingIcon = AppIconType.INFO,
                            onClick = {
                                onClose()
                                onOpenShortcuts()
                            }
                        )
                        MenuItem(
                            icon = AppIconType.TV,
                            label = "TV remote mode",
                            badgeText = if (tvMode) "On" else "Off",
                            onClick = onToggleTvMode
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    val d = LocalDimens.current
    Text(
        text = title,
        color = MuteColor,
        fontSize = d.sp(0.68f),
        fontWeight = FontWeight.SemiBold,
        letterSpacing = d.sp(0.16f),
        modifier = Modifier.padding(
            start = d.rem(1.25f),
            top = d.rem(1.1f),
            bottom = d.rem(0.5f)
        )
    )
}

@Composable
fun MenuItem(
    icon: AppIconType,
    label: String,
    onClick: () -> Unit,
    kbdHint: String? = null,
    trailingText: String? = null,
    badgeCount: Int? = null,
    badgeText: String? = null,
    trailingIcon: AppIconType? = null,
    isCurrent: Boolean = false
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(d.rem(0.9f))
    val bgColor = when {
        isCurrent || isFocused -> S3Color
        else -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = d.rem(0.5f), vertical = d.rem(0.1f))
            .tvFocusRing(isFocused, d.rem(0.9f))
            .clip(shape)
            .background(bgColor)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(horizontal = d.rem(0.75f), vertical = d.rem(0.7f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.rem(0.9f))
    ) {
        AppIcon(
            icon = icon,
            tint = if (isCurrent || isFocused) FgColor else SubColor,
            size = d.rem(1.25f)
        )

        Text(
            text = label,
            color = if (isCurrent || isFocused) FgColor else SubColor,
            fontSize = d.sp(0.9f),
            modifier = Modifier.weight(1f)
        )

        if (kbdHint != null) {
            val kbdShape = RoundedCornerShape(d.rem(0.4f))
            Box(
                modifier = Modifier
                    .clip(kbdShape)
                    .background(S3Color)
                    .border(BorderStroke(d.px(1f), S4Color), kbdShape)
                    .padding(horizontal = d.rem(0.4f), vertical = d.rem(0.1f))
            ) {
                Text(
                    text = kbdHint,
                    color = FgColor,
                    fontSize = d.sp(0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (trailingText != null) {
            Text(
                text = trailingText,
                color = MuteColor,
                fontSize = d.sp(0.75f)
            )
        }

        if (badgeCount != null) {
            val badgeShape = RoundedCornerShape(99.dp)
            Box(
                modifier = Modifier
                    .clip(badgeShape)
                    .background(S4Color)
                    .padding(horizontal = d.rem(0.5f), vertical = d.rem(0.1f))
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = FgColor,
                    fontSize = d.sp(0.7f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (badgeText != null) {
            val badgeShape = RoundedCornerShape(99.dp)
            Box(
                modifier = Modifier
                    .clip(badgeShape)
                    .background(S4Color)
                    .padding(horizontal = d.rem(0.5f), vertical = d.rem(0.1f))
            ) {
                Text(
                    text = badgeText,
                    color = FgColor,
                    fontSize = d.sp(0.7f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (trailingIcon != null) {
            AppIcon(
                icon = trailingIcon,
                tint = MuteColor,
                size = d.rem(1f)
            )
        }
    }
}
