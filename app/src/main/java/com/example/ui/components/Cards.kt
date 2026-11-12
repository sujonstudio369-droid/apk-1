package com.example.ui.components

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.Device
import com.example.data.DeviceType
import com.example.data.Folder
import com.example.data.Formatters
import com.example.data.Photo
import com.example.ui.theme.AccColor
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
fun DeviceCard(
    device: Device,
    onClick: () -> Unit,
    onOptionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val cardShape = RoundedCornerShape(d.rem(1.75f))
    val bgColor = if (isFocused) S3Color else S2Color
    val borderColor = if (isFocused) MuteColor else LineColor

    val usedGb = Formatters.fmtGB(device.usedBytes)
    val totalGb = Formatters.fmtGB(device.totalBytes)
    val pct = if (device.totalBytes > 0) {
        ((device.usedBytes.toDouble() / device.totalBytes) * 100).toInt().coerceIn(0, 100)
    } else 0
    val freeBytes = maxOf(0L, device.totalBytes - device.usedBytes)
    val freeGb = Formatters.fmtGB(freeBytes)

    Column(
        modifier = modifier
            .heightIn(min = d.rem(16f))
            .tvFocusRing(isFocused, d.rem(1.75f))
            .clip(cardShape)
            .background(bgColor)
            .border(BorderStroke(d.px(1f), borderColor), cardShape)
            .alpha(if (device.connected) 1f else 0.5f)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(d.rem(1.5f)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            val iconShape = RoundedCornerShape(d.rem(1.3f))
            Box(
                modifier = Modifier
                    .size(d.rem(4.5f))
                    .clip(iconShape)
                    .background(S1Color)
                    .border(BorderStroke(d.px(1f), LineColor), iconShape),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    icon = if (device.type == DeviceType.USB) AppIconType.USB else AppIconType.SD,
                    tint = AccColor,
                    size = d.rem(2.25f),
                    strokeWidth = 1.4f
                )
            }

            Box(modifier = Modifier.offset(x = d.rem(0.5f), y = -d.rem(0.5f))) {
                BtnIcon(
                    icon = AppIconType.ELLIPSIS,
                    onClick = onOptionsClick,
                    contentDescription = "${device.name} options"
                )
            }
        }

        Spacer(modifier = Modifier.height(d.rem(2.5f)))

        // Bottom block
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = device.name,
                    color = FgColor,
                    fontSize = d.sp(1.25f),
                    fontWeight = FontWeight.SemiBold
                )
                if (!device.connected) {
                    Spacer(modifier = Modifier.size(d.rem(0.5f)))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(99.dp))
                            .background(S4Color)
                            .padding(horizontal = d.rem(0.5f), vertical = d.rem(0.1f))
                    ) {
                        Text(
                            text = "Disconnected",
                            color = FgColor,
                            fontSize = d.sp(0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(d.rem(0.25f)))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = usedGb,
                    color = FgColor,
                    fontSize = d.sp(0.875f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = " / $totalGb",
                    color = SubColor,
                    fontSize = d.sp(0.875f)
                )
            }

            Spacer(modifier = Modifier.height(d.rem(1f)))

            UsageBar(percentage = pct.toFloat(), height = 6.dp)

            Spacer(modifier = Modifier.height(d.rem(0.5f)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$pct% used",
                    color = MuteColor,
                    fontSize = d.sp(0.75f)
                )
                Text(
                    text = "$freeGb free",
                    color = MuteColor,
                    fontSize = d.sp(0.75f)
                )
            }
        }
    }
}

@Composable
fun FavoritesTile(
    photoCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape = RoundedCornerShape(d.rem(1.4f))
    val bgColor = if (isFocused) S2Color else S1Color
    val borderColor = if (isFocused) MuteColor else LineColor

    Row(
        modifier = modifier
            .fillMaxWidth()
            .tvFocusRing(isFocused, d.rem(1.4f))
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
            .padding(horizontal = d.rem(1.25f), vertical = d.rem(1.1f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.rem(1f))
    ) {
        val iconShape = RoundedCornerShape(d.rem(0.9f))
        Box(
            modifier = Modifier
                .size(d.rem(2.75f))
                .clip(iconShape)
                .background(S2Color)
                .border(BorderStroke(d.px(1f), LineColor), iconShape),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                icon = AppIconType.HEART,
                tint = AccColor,
                size = d.rem(1.25f)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Favorites",
                color = FgColor,
                fontSize = d.sp(0.875f),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = Formatters.plural(photoCount, "photo"),
                color = MuteColor,
                fontSize = d.sp(0.75f)
            )
        }

        AppIcon(
            icon = AppIconType.CHEVRON_RIGHT,
            tint = MuteColor,
            size = d.rem(1.25f)
        )
    }
}

@Composable
fun FolderCard(
    folder: Folder,
    minTileSize: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val isSmall = minTileSize <= d.rem(5.5f)
    val cardShape = RoundedCornerShape(if (isSmall) d.rem(1.1f) else d.rem(1.6f))
    val bgColor = if (isFocused) S3Color else S2Color
    val borderColor = if (isFocused) MuteColor else LineColor

    Column(
        modifier = modifier
            .heightIn(min = minTileSize * 0.95f)
            .tvFocusRing(isFocused, if (isSmall) d.rem(1.1f) else d.rem(1.6f))
            .clip(cardShape)
            .background(bgColor)
            .border(BorderStroke(d.px(1f), borderColor), cardShape)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(
                horizontal = if (isSmall) d.rem(0.6f) else d.rem(1f),
                vertical = if (isSmall) d.rem(0.8f) else d.rem(1.4f)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val boxSize = if (isSmall) d.rem(2.2f) else d.rem(3.5f)
        val iconSize = if (isSmall) d.rem(1.9f) else d.rem(3.25f)

        Box(
            modifier = Modifier.size(boxSize),
            contentAlignment = Alignment.Center
        ) {
            AppIcon(
                icon = AppIconType.FOLDER,
                tint = AccColor,
                size = iconSize,
                strokeWidth = 1.25f
            )
        }

        Spacer(modifier = Modifier.height(if (isSmall) d.rem(0.4f) else d.rem(0.9f)))

        Text(
            text = folder.name,
            color = FgColor,
            fontSize = if (isSmall) d.sp(0.75f) else d.sp(0.875f),
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(d.rem(0.2f)))

        Text(
            text = "${Formatters.plural(folder.photos.size, "photo")} · ${Formatters.fmtBytes(folder.size)}",
            color = MuteColor,
            fontSize = if (isSmall) d.sp(0.65f) else d.sp(0.75f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PhotoCard(
    photo: Photo,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val cardShape = RoundedCornerShape(d.rem(1.4f))
    val bgColor = if (isFocused) S3Color else S2Color
    val borderColor = if (isFocused) MuteColor else LineColor

    val brightnessMatrix = remember(isFocused) {
        if (isFocused) {
            ColorMatrix(
                floatArrayOf(
                    1.12f, 0f, 0f, 0f, 0f,
                    0f, 1.12f, 0f, 0f, 0f,
                    0f, 0f, 1.12f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        } else null
    }

    Box(
        modifier = modifier
            .tvFocusRing(isFocused, d.rem(1.4f))
            .clip(cardShape)
            .background(bgColor)
            .border(BorderStroke(d.px(1f), borderColor), cardShape)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(d.rem(0.5f))
    ) {
        val imgShape = RoundedCornerShape(d.rem(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(imgShape)
                .background(S1Color)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photo.uri)
                    .crossfade(400)
                    .build(),
                contentDescription = photo.name,
                contentScale = ContentScale.Crop,
                colorFilter = brightnessMatrix?.let { ColorFilter.colorMatrix(it) },
                modifier = Modifier.fillMaxSize()
            )

            // Caption overlay (always visible on TV)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xBF000000))
                        )
                    )
                    .padding(
                        start = d.rem(0.7f),
                        end = d.rem(0.7f),
                        bottom = d.rem(0.55f),
                        top = d.rem(1.5f)
                    )
            ) {
                Text(
                    text = photo.name,
                    color = Color(0xFFEEEEEE),
                    fontSize = d.sp(0.75f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Heart badge top right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = d.rem(0.5f), end = d.rem(0.5f))
                    .size(d.rem(2f))
                    .clip(CircleShape)
                    .background(Color(0xB3141414))
                    .clickable(role = Role.Button, onClick = onFavClick),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(
                    icon = AppIconType.HEART,
                    tint = if (isFavorite) AccColor else FgColor,
                    size = d.rem(1f),
                    isFilled = isFavorite
                )
            }
        }
    }
}
