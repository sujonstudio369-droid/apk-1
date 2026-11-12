package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.Btn
import com.example.ui.theme.AccColor
import com.example.ui.theme.AccInkColor
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.SubColor

@Composable
fun PermissionScreen(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(d.rem(2f)),
        contentAlignment = Alignment.Center
    ) {
        val cardShape = RoundedCornerShape(d.rem(1.75f))
        Column(
            modifier = Modifier
                .widthIn(min = d.rem(24f), max = d.rem(28f))
                .clip(cardShape)
                .background(S2Color)
                .border(BorderStroke(d.px(1f), LineColor), cardShape)
                .padding(d.rem(2f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                    icon = AppIconType.IMAGE,
                    tint = AccColor,
                    size = d.rem(2.25f),
                    strokeWidth = 1.4f
                )
            }

            Spacer(modifier = Modifier.height(d.rem(1.25f)))

            Text(
                text = "Allow access to your photos",
                color = FgColor,
                fontSize = d.sp(1.25f),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(d.rem(0.5f)))

            Text(
                text = "Photo Viewer needs permission to read photos on this TV and on connected USB drives.",
                color = SubColor,
                fontSize = d.sp(0.875f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(d.rem(1.5f)))

            Btn(
                onClick = onRequestPermission,
                isAccent = true
            ) {
                Text(
                    text = "Allow access",
                    color = AccInkColor,
                    fontSize = d.sp(0.875f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
