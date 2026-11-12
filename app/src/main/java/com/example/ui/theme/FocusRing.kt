package com.example.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.tvFocusRing(
    isFocused: Boolean,
    cornerRadius: Dp = 0.dp,
    gapWidth: Dp = 3.dp,
    ringWidth: Dp = 3.dp
): Modifier = if (!isFocused) this else this.drawWithContent {
    drawContent()

    val gapPx = gapWidth.toPx()
    val ringPx = ringWidth.toPx()
    val radiusPx = cornerRadius.toPx()

    // 1. Draw 3px gap in #141414 outside the bounds
    val gapOffset = gapPx / 2f
    val gapRect = RoundRect(
        left = -gapOffset,
        top = -gapOffset,
        right = size.width + gapOffset,
        bottom = size.height + gapOffset,
        cornerRadius = CornerRadius(radiusPx + gapOffset, radiusPx + gapOffset)
    )
    val gapPath = Path().apply { addRoundRect(gapRect) }
    drawPath(
        path = gapPath,
        color = BgColor,
        style = Stroke(width = gapPx)
    )

    // 2. Draw 3px ring in #F4F4F4 outside the gap
    val ringOffset = gapPx + (ringPx / 2f)
    val ringRect = RoundRect(
        left = -ringOffset,
        top = -ringOffset,
        right = size.width + ringOffset,
        bottom = size.height + ringOffset,
        cornerRadius = CornerRadius(radiusPx + ringOffset, radiusPx + ringOffset)
    )
    val ringPath = Path().apply { addRoundRect(ringRect) }
    drawPath(
        path = ringPath,
        color = FocusRingColor,
        style = Stroke(width = ringPx)
    )
}
