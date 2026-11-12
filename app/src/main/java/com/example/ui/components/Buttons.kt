package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccColor
import com.example.ui.theme.AccInkColor
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.CircleBgColor
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor
import com.example.ui.theme.tvFocusRing

@Composable
fun BtnIcon(
    icon: AppIconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = LocalDimens.current.rem(2.5f),
    iconSize: Dp = LocalDimens.current.rem(1.25f),
    contentDescription: String? = null
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val radius = d.rem(0.9f)
    val shape = RoundedCornerShape(radius)

    val bgColor = if (isFocused) S3Color else Color.Transparent
    val tint = if (isFocused) FgColor else SubColor

    Box(
        modifier = modifier
            .size(size)
            .scale(if (isPressed) 0.94f else 1.0f)
            .alpha(if (enabled) 1.0f else 0.3f)
            .tvFocusRing(isFocused, radius)
            .clip(shape)
            .background(bgColor)
            .focusable(enabled, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
                role = Role.Button
            ),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(icon = icon, tint = tint, size = iconSize)
    }
}

@Composable
fun Btn(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isAccent: Boolean = false,
    contentPadding: PaddingValues? = null,
    content: @Composable RowScope.() -> Unit
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val radius = d.rem(0.9f)
    val shape = RoundedCornerShape(radius)

    val bgColor = when {
        isAccent -> if (isFocused) Color(0xFFECECEC) else AccColor
        isFocused -> S3Color
        else -> S2Color
    }
    val borderColor = when {
        isAccent -> Color.Transparent
        isFocused -> S4Color
        else -> LineColor
    }

    Row(
        modifier = modifier
            .height(d.rem(2.5f))
            .scale(if (isPressed) 0.96f else 1.0f)
            .alpha(if (enabled) 1.0f else 0.3f)
            .tvFocusRing(isFocused, radius)
            .clip(shape)
            .background(bgColor)
            .border(BorderStroke(d.px(1f), borderColor), shape)
            .focusable(enabled, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
                role = Role.Button
            )
            .padding(contentPadding ?: PaddingValues(horizontal = d.rem(1f))),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun CircleButton(
    icon: AppIconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isSmall: Boolean = false,
    contentDescription: String? = null
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val btnSize = if (isSmall) d.rem(2.25f) else d.rem(2.75f)
    val iconSize = if (isSmall) d.rem(1.1f) else d.rem(1.25f)

    val bgColor = if (isFocused) AccColor else CircleBgColor
    val tint = if (isFocused) AccInkColor else FgColor

    Box(
        modifier = modifier
            .size(btnSize)
            .scale(if (isPressed) 0.94f else 1.0f)
            .alpha(if (enabled) 1.0f else 0.3f)
            .tvFocusRing(isFocused, btnSize / 2f)
            .clip(CircleShape)
            .background(bgColor)
            .border(BorderStroke(d.px(1f), S4Color), CircleShape)
            .focusable(enabled, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
                role = Role.Button
            ),
        contentAlignment = Alignment.Center
    ) {
        AppIcon(icon = icon, tint = tint, size = iconSize)
    }
}
