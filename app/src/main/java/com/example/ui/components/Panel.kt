package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.data.Device
import com.example.data.DeviceType
import com.example.data.Folder
import com.example.data.Formatters
import com.example.ui.theme.AccColor
import com.example.ui.theme.AccInkColor
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.MuteColor
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor
import com.example.ui.theme.tvFocusRing

@Composable
fun StoragePanel(
    devices: List<Device>,
    favoriteCount: Int,
    currentDeviceId: String?,
    isFavRoute: Boolean,
    isOpen: Boolean,
    onToggle: () -> Unit,
    onDeviceClick: (Device) -> Unit,
    onFavClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val targetWidth = if (isOpen) d.rem(17f) else 0.dp
    val animatedWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = tween(durationMillis = 300),
        label = "storage_panel_width"
    )

    Box(modifier = modifier.fillMaxHeight()) {
        Box(
            modifier = Modifier
                .width(animatedWidth)
                .fillMaxHeight()
                .background(S1Color)
                .border(BorderStroke(if (isOpen) d.px(1f) else 0.dp, LineColor))
        ) {
            if (isOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = d.rem(0.75f), vertical = d.rem(1f)),
                    verticalArrangement = Arrangement.spacedBy(d.rem(0.375f))
                ) {
                    Text(
                        text = "STORAGE",
                        color = MuteColor,
                        fontSize = d.sp(0.7f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            start = d.rem(0.75f),
                            top = d.rem(0.25f),
                            bottom = d.rem(0.75f)
                        )
                    )

                    devices.filter { it.connected }.forEach { dev ->
                        val isCurrent = currentDeviceId == dev.id && !isFavRoute
                        val usedGb = Formatters.fmtGB(dev.usedBytes)
                        val totalGb = Formatters.fmtGB(dev.totalBytes)
                        val pct = if (dev.totalBytes > 0) {
                            ((dev.usedBytes.toDouble() / dev.totalBytes) * 100).toInt()
                        } else 0

                        PanelRow(
                            icon = if (dev.type == DeviceType.USB) AppIconType.USB else AppIconType.SD,
                            title = dev.name,
                            subline = "$usedGb / $totalGb",
                            usagePercent = pct.toFloat(),
                            isCurrent = isCurrent,
                            onClick = { onDeviceClick(dev) }
                        )
                    }

                    PanelRow(
                        icon = AppIconType.HEART,
                        title = "Favorites",
                        subline = Formatters.plural(favoriteCount, "photo"),
                        usagePercent = null,
                        isCurrent = isFavRoute,
                        onClick = onFavClick
                    )
                }
            }
        }

        // 18px x 56px panel tab handle
        PanelTabHandle(
            isOpen = isOpen,
            onClick = onToggle,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 18.dp)
                .zIndex(15f)
        )
    }
}

@Composable
fun FoldersPanel(
    deviceName: String,
    folders: List<Folder>,
    currentFolderId: String?,
    isOpen: Boolean,
    onToggle: () -> Unit,
    onFolderClick: (Folder) -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val targetWidth = if (isOpen) d.rem(17f) else 0.dp
    val animatedWidth by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = tween(durationMillis = 300),
        label = "folders_panel_width"
    )

    Box(modifier = modifier.fillMaxHeight()) {
        Box(
            modifier = Modifier
                .width(animatedWidth)
                .fillMaxHeight()
                .background(S1Color)
                .border(BorderStroke(if (isOpen) d.px(1f) else 0.dp, LineColor))
        ) {
            if (isOpen) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = d.rem(0.75f), vertical = d.rem(1f)),
                    verticalArrangement = Arrangement.spacedBy(d.rem(0.375f))
                ) {
                    Text(
                        text = deviceName.uppercase(),
                        color = MuteColor,
                        fontSize = d.sp(0.7f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            start = d.rem(0.75f),
                            top = d.rem(0.25f),
                            bottom = d.rem(0.75f)
                        )
                    )

                    folders.forEach { folder ->
                        val isCurrent = currentFolderId == folder.id
                        PanelRow(
                            icon = if (isCurrent) AppIconType.FOLDER_OPEN else AppIconType.FOLDER,
                            title = folder.name,
                            subline = Formatters.plural(folder.photos.size, "photo"),
                            usagePercent = null,
                            isCurrent = isCurrent,
                            onClick = { onFolderClick(folder) }
                        )
                    }
                }
            }
        }

        PanelTabHandle(
            isOpen = isOpen,
            onClick = onToggle,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 18.dp)
                .zIndex(15f)
        )
    }
}

@Composable
fun PanelRow(
    icon: AppIconType,
    title: String,
    subline: String,
    usagePercent: Float?,
    isCurrent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(d.rem(1f))
    val bgColor = when {
        isCurrent -> S3Color
        isFocused -> S2Color
        else -> Color.Transparent
    }
    val borderColor = when {
        isCurrent -> S4Color
        else -> Color.Transparent
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .tvFocusRing(isFocused, d.rem(1f))
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
            .padding(horizontal = d.rem(0.7f), vertical = d.rem(0.6f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.rem(0.75f))
    ) {
        val iconShape = RoundedCornerShape(d.rem(0.8f))
        Box(
            modifier = Modifier
                .size(d.rem(2.5f))
                .clip(iconShape)
                .background(S1Color)
                .border(BorderStroke(d.px(1f), LineColor), iconShape),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                icon = icon,
                tint = AccColor,
                size = d.rem(1.25f)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isCurrent || isFocused) FgColor else SubColor,
                fontSize = d.sp(0.875f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subline,
                color = MuteColor,
                fontSize = d.sp(0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (usagePercent != null) {
                Spacer(modifier = Modifier.height(d.rem(0.2f)))
                UsageBar(percentage = usagePercent, height = 4.dp, animate = false)
            }
        }
    }
}

@Composable
fun PanelTabHandle(
    isOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val rotation by animateFloatAsState(
        targetValue = if (isOpen) 0f else 180f,
        animationSpec = tween(durationMillis = 300),
        label = "panel_tab_rotation"
    )

    val shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
    val bgColor = if (isFocused) AccColor else S2Color
    val iconTint = if (isFocused) AccInkColor else SubColor

    Box(
        modifier = modifier
            .size(width = 18.dp, height = 56.dp)
            .tvFocusRing(isFocused, 12.dp)
            .clip(shape)
            .background(bgColor)
            .border(BorderStroke(d.px(1f), LineColor), shape)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            ),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(
            icon = AppIconType.CHEVRON_LEFT,
            tint = iconTint,
            size = 14.dp,
            modifier = Modifier.rotate(rotation)
        )
    }
}
