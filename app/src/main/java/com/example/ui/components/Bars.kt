package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.S4Color

@Composable
fun UsageBar(
    percentage: Float, // 0f..100f
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    animate: Boolean = true
) {
    val d = LocalDimens.current
    var targetPercent by remember { mutableFloatStateOf(if (animate) 0f else percentage) }

    LaunchedEffect(percentage) {
        targetPercent = percentage
    }

    val animatedWidth by animateFloatAsState(
        targetValue = (targetPercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = if (animate) 900 else 0, easing = FastOutSlowInEasing),
        label = "usage_bar_fill"
    )

    val shape = RoundedCornerShape(99.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(S4Color)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedWidth)
                .fillMaxHeight()
                .clip(shape)
                .background(AccColor)
        )
    }
}
