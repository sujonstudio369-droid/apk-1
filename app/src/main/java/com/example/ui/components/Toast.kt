package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FgColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.S2Color
import com.example.ui.theme.S4Color

@Composable
fun ToastOverlay(
    message: String?,
    bottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = bottomPadding),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = message != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            if (message != null) {
                val shape = RoundedCornerShape(99.dp)
                Box(
                    modifier = Modifier
                        .shadow(16.dp, shape)
                        .clip(shape)
                        .background(S2Color)
                        .border(d.px(1f), S4Color, shape)
                        .padding(horizontal = d.rem(1.1f), vertical = d.rem(0.65f))
                ) {
                    Text(
                        text = message,
                        color = FgColor,
                        fontSize = d.sp(0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
