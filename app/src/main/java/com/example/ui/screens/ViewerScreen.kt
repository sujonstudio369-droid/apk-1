package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.Formatters
import com.example.data.Photo
import com.example.state.ViewerState
import com.example.ui.components.BtnIcon
import com.example.ui.components.CircleButton
import com.example.ui.theme.AccColor
import com.example.ui.theme.AccInkColor
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.ModalScrimColor
import com.example.ui.theme.MuteColor
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor
import com.example.ui.theme.ViewerBgColor
import com.example.ui.theme.tvFocusRing

@Composable
fun ViewerScreen(
    state: ViewerState,
    isFavorite: Boolean,
    onClose: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onPanUp: () -> Unit,
    onPanDown: () -> Unit,
    onRotate: () -> Unit,
    onResetZoom: () -> Unit,
    onToggleToolbar: () -> Unit,
    onToggleStrip: () -> Unit,
    onToggleDetails: () -> Unit,
    onOpenMenu: () -> Unit,
    onSelectThumb: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current
    val context = LocalContext.current
    val photo = state.currentPhoto

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ViewerBgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Viewer Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(d.rem(4.25f))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xF20F0F0F), Color(0x990F0F0F))
                        )
                    )
                    .border(BorderStroke(d.px(1f), LineColor))
                    .padding(horizontal = d.rem(1.25f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(d.rem(0.5f))
            ) {
                CircleButton(
                    icon = AppIconType.X,
                    onClick = onClose,
                    contentDescription = "Close viewer"
                )

                // File name + count
                Column(
                    modifier = Modifier
                        .padding(start = d.rem(0.5f))
                        .weight(1f)
                ) {
                    val title = (photo?.name ?: "") + if (isFavorite) " ♥" else ""
                    Text(
                        text = title,
                        color = FgColor,
                        fontSize = d.sp(0.875f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${state.idx + 1} of ${state.list.size}",
                        color = MuteColor,
                        fontSize = d.sp(0.75f)
                    )
                }

                // Toolbar pill
                AnimatedVisibility(
                    visible = state.toolbarOpen,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    val pillShape = RoundedCornerShape(d.rem(1.1f))
                    Row(
                        modifier = Modifier
                            .clip(pillShape)
                            .background(S2Color)
                            .border(BorderStroke(d.px(1f), LineColor), pillShape)
                            .padding(d.rem(0.25f)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(d.rem(0.2f))
                    ) {
                        BtnIcon(
                            icon = AppIconType.ZOOM_IN,
                            onClick = onZoomIn,
                            size = d.rem(2.4f),
                            contentDescription = "Zoom in"
                        )
                        BtnIcon(
                            icon = AppIconType.ZOOM_OUT,
                            onClick = onZoomOut,
                            size = d.rem(2.4f),
                            contentDescription = "Zoom out"
                        )

                        // Vertical separator
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = d.rem(1.4f))
                                .background(S4Color)
                                .padding(horizontal = d.rem(0.2f))
                        )

                        BtnIcon(
                            icon = AppIconType.CHEVRON_UP,
                            onClick = onPanUp,
                            size = d.rem(2.4f),
                            enabled = state.isZoomed,
                            contentDescription = "Pan up"
                        )
                        BtnIcon(
                            icon = AppIconType.CHEVRON_DOWN,
                            onClick = onPanDown,
                            size = d.rem(2.4f),
                            enabled = state.isZoomed,
                            contentDescription = "Pan down"
                        )

                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = d.rem(1.4f))
                                .background(S4Color)
                                .padding(horizontal = d.rem(0.2f))
                        )

                        BtnIcon(
                            icon = AppIconType.ROTATE_CW,
                            onClick = onRotate,
                            size = d.rem(2.4f),
                            contentDescription = "Rotate"
                        )

                        // Zoom chip
                        val zoomText = "${(state.scale * state.fit * 100).toInt()}%"
                        val chipInteraction = remember { MutableInteractionSource() }
                        val chipFocused by chipInteraction.collectIsFocusedAsState()
                        val chipShape = RoundedCornerShape(d.rem(0.9f))

                        Box(
                            modifier = Modifier
                                .widthIn(min = d.rem(3.4f))
                                .height(d.rem(2.4f))
                                .tvFocusRing(chipFocused, d.rem(0.9f))
                            .clip(chipShape)
                            .background(if (chipFocused) S3Color else Color.Transparent)
                            .focusable(true, chipInteraction)
                            .clickable(
                                interactionSource = chipInteraction,
                                indication = null,
                                onClick = onResetZoom,
                                role = Role.Button
                            )
                            .padding(horizontal = d.rem(0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = zoomText,
                                color = if (chipFocused) FgColor else SubColor,
                                fontSize = d.sp(0.78f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Toolbar toggle button
                val toggleRotation by animateFloatAsState(
                    targetValue = if (state.toolbarOpen) 0f else 180f,
                    label = "toolbar_toggle_rot"
                )
                CircleButton(
                    icon = AppIconType.CHEVRON_UP,
                    onClick = onToggleToolbar,
                    isSmall = true,
                    modifier = Modifier.rotate(toggleRotation),
                    contentDescription = "Toggle toolbar"
                )

                // Menu button
                BtnIcon(
                    icon = AppIconType.MENU,
                    onClick = onOpenMenu,
                    contentDescription = "Open menu"
                )
            }

            // 2. Stage (Photo canvas)
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (photo != null) {
                    val imgShape = RoundedCornerShape(6.dp)

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(photo.uri)
                            .crossfade(300)
                            .build(),
                        contentDescription = photo.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = state.tx
                                translationY = state.ty
                                scaleX = state.scale
                                scaleY = state.scale
                                rotationZ = state.rotation
                            }
                            .shadow(30.dp, imgShape)
                            .clip(imgShape)
                    )
                }

                // Prev circle button (left)
                CircleButton(
                    icon = AppIconType.CHEVRON_LEFT,
                    onClick = onPrev,
                    enabled = state.idx > 0,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = d.rem(1.5f)),
                    contentDescription = "Previous photo"
                )

                // Next circle button (right)
                CircleButton(
                    icon = AppIconType.CHEVRON_RIGHT,
                    onClick = onNext,
                    enabled = state.idx < state.list.size - 1,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = d.rem(1.5f)),
                    contentDescription = "Next photo"
                )

                // Center Badge (zoom percent, fit, rotated, slideshow)
                androidx.compose.animation.AnimatedVisibility(
                    visible = state.badgeText != null,
                    enter = fadeIn() + slideInVertically { -it },
                    exit = fadeOut() + slideOutVertically { -it },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = d.rem(1f))
                ) {
                    val badgeShape = RoundedCornerShape(99.dp)
                    Box(
                        modifier = Modifier
                            .clip(badgeShape)
                            .background(Color(0xE6202020))
                            .border(BorderStroke(d.px(1f), S4Color), badgeShape)
                            .padding(horizontal = d.rem(0.9f), vertical = d.rem(0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.badgeText ?: "",
                            color = FgColor,
                            fontSize = d.sp(0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Details Card Overlay
                if (state.showInfo && photo != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ModalScrimColor)
                            .clickable(onClick = onToggleDetails),
                        contentAlignment = Alignment.Center
                    ) {
                        val cardShape = RoundedCornerShape(d.rem(1.6f))
                        Column(
                            modifier = Modifier
                                .widthIn(min = d.rem(22f), max = d.rem(24f))
                                .clickable(enabled = false) {}
                                .shadow(30.dp, cardShape)
                                .clip(cardShape)
                                .background(S2Color)
                                .border(BorderStroke(d.px(1f), S4Color), cardShape)
                                .padding(horizontal = d.rem(1.4f), vertical = d.rem(1.25f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = d.rem(0.75f)),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Details",
                                    color = FgColor,
                                    fontSize = d.sp(0.875f),
                                    fontWeight = FontWeight.SemiBold
                                )
                                BtnIcon(
                                    icon = AppIconType.X,
                                    onClick = onToggleDetails,
                                    size = d.rem(1.9f),
                                    contentDescription = "Close details"
                                )
                            }

                            val rows = listOf(
                                Pair("Name", photo.name),
                                Pair("Type", photo.mime),
                                Pair("Dimensions", "${photo.width} × ${photo.height} px"),
                                Pair("Size", Formatters.fmtBytes(photo.size)),
                                Pair("Date", Formatters.fmtDate(photo.date)),
                                Pair("Location", "${photo.deviceId} › ${photo.folderId}")
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(d.rem(0.55f))) {
                                rows.forEach { (label, value) ->
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = label,
                                            color = MuteColor,
                                            fontSize = d.sp(0.8f),
                                            modifier = Modifier.width(d.rem(5.5f))
                                        )
                                        Text(
                                            text = value,
                                            color = FgColor,
                                            fontSize = d.sp(0.8f),
                                            modifier = Modifier.weight(1f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Thumbnail Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(S1Color)
                    .border(BorderStroke(if (state.stripOpen) d.px(1f) else 0.dp, LineColor))
            ) {
                // Strip toggle tab handle
                val toggleTabRotation by animateFloatAsState(
                    targetValue = if (state.stripOpen) 0f else 180f,
                    label = "strip_toggle_rot"
                )
                val tabShape = RoundedCornerShape(topStart = d.rem(0.9f), topEnd = d.rem(0.9f))
                val tabInteraction = remember { MutableInteractionSource() }
                val tabFocused by tabInteraction.collectIsFocusedAsState()

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = -d.rem(1.75f))
                        .size(width = d.rem(5f), height = d.rem(1.75f))
                        .tvFocusRing(tabFocused, d.rem(0.9f))
                        .clip(tabShape)
                        .background(if (tabFocused) AccColor else S2Color)
                        .border(BorderStroke(d.px(1f), S4Color), tabShape)
                        .focusable(true, tabInteraction)
                        .clickable(
                            interactionSource = tabInteraction,
                            indication = null,
                            onClick = onToggleStrip,
                            role = Role.Button
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        icon = AppIconType.CHEVRON_DOWN,
                        tint = if (tabFocused) AccInkColor else FgColor,
                        size = d.rem(1.3f),
                        strokeWidth = 2.25f,
                        modifier = Modifier.rotate(toggleTabRotation)
                    )
                }

                // Strip horizontal list
                val stripHeight by animateDpAsState(
                    targetValue = if (state.stripOpen) d.rem(6f) else 0.dp,
                    label = "strip_height"
                )

                if (state.stripOpen) {
                    val scrollState = rememberScrollState()

                    LaunchedEffect(state.idx) {
                        scrollState.animateScrollTo((state.idx * 100).coerceAtLeast(0))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(stripHeight)
                            .horizontalScroll(scrollState)
                            .padding(horizontal = d.rem(1f), vertical = d.rem(0.8f)),
                        horizontalArrangement = Arrangement.spacedBy(d.rem(0.6f)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        state.list.forEachIndexed { i, p ->
                            val isActive = i == state.idx
                            val thumbShape = RoundedCornerShape(d.rem(0.85f))
                            val thumbInteraction = remember { MutableInteractionSource() }
                            val thumbFocused by thumbInteraction.collectIsFocusedAsState()

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .tvFocusRing(thumbFocused, d.rem(0.85f))
                                    .clip(thumbShape)
                                    .background(S2Color)
                                    .border(
                                        BorderStroke(
                                            if (isActive) d.px(2f) else 0.dp,
                                            if (isActive) AccColor else Color.Transparent
                                        ),
                                        thumbShape
                                    )
                                    .alpha(if (isActive) 1f else 0.5f)
                                    .focusable(true, thumbInteraction)
                                    .clickable(
                                        interactionSource = thumbInteraction,
                                        indication = null,
                                        onClick = { onSelectThumb(i) },
                                        role = Role.Button
                                    )
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(p.uri)
                                        .build(),
                                    contentDescription = p.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
