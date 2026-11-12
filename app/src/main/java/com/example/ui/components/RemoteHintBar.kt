package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.state.ModalType
import com.example.state.PopoverType
import com.example.state.Route
import com.example.state.ViewerState
import com.example.ui.theme.ColorBlue
import com.example.ui.theme.ColorGreen
import com.example.ui.theme.ColorRed
import com.example.ui.theme.ColorYellow
import com.example.ui.theme.FgColor
import com.example.ui.theme.HintBarBgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor

sealed class HintItem {
    data class KeyHint(val key: String, val label: String) : HintItem()
    data class ColorHint(val color: Color, val label: String) : HintItem()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemoteHintBar(
    route: Route,
    viewerState: ViewerState,
    activeModal: ModalType,
    activePopover: PopoverType,
    isDrawerOpen: Boolean,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current

    val hints = when {
        activeModal != ModalType.NONE -> listOf(
            HintItem.KeyHint("▲ ▼", "Scroll"),
            HintItem.KeyHint("OK", "Select"),
            HintItem.KeyHint("BACK", "Close")
        )
        activePopover != PopoverType.NONE -> listOf(
            HintItem.KeyHint("▲ ▼", "Move"),
            HintItem.KeyHint("OK", "Select"),
            HintItem.KeyHint("BACK", "Close")
        )
        isDrawerOpen -> listOf(
            HintItem.KeyHint("▲ ▼", "Move"),
            HintItem.KeyHint("OK", "Select"),
            HintItem.KeyHint("MENU", "Close")
        )
        viewerState.open -> when {
            viewerState.showInfo -> listOf(
                HintItem.KeyHint("OK", "Close"),
                HintItem.KeyHint("BACK", "Close")
            )
            viewerState.uiMode -> listOf(
                HintItem.KeyHint("◀ ▲ ▼ ▶", "Move"),
                HintItem.KeyHint("OK", "Select"),
                HintItem.KeyHint("BACK", "Back to photo")
            )
            viewerState.isZoomed -> listOf(
                HintItem.KeyHint("◀ ▲ ▼ ▶", "Pan"),
                HintItem.KeyHint("OK", "Fit"),
                HintItem.KeyHint("CH±", "Photo"),
                HintItem.ColorHint(ColorRed, "Favorite"),
                HintItem.ColorHint(ColorYellow, "Rotate"),
                HintItem.KeyHint("BACK", "Fit")
            )
            else -> listOf(
                HintItem.KeyHint("◀ ▶", "Photo"),
                HintItem.KeyHint("OK", "Zoom"),
                HintItem.KeyHint("▲", "Controls"),
                HintItem.KeyHint("▼", "Thumbnails"),
                HintItem.ColorHint(ColorRed, "Favorite"),
                HintItem.ColorHint(ColorGreen, "Slideshow"),
                HintItem.ColorHint(ColorYellow, "Rotate"),
                HintItem.ColorHint(ColorBlue, "Details"),
                HintItem.KeyHint("BACK", "Close")
            )
        }
        route is Route.Home -> listOf(
            HintItem.KeyHint("◀ ▲ ▼ ▶", "Move"),
            HintItem.KeyHint("OK", "Open"),
            HintItem.KeyHint("INFO", "Device options"),
            HintItem.KeyHint("MENU", "Menu")
        )
        route is Route.Device -> listOf(
            HintItem.KeyHint("◀ ▲ ▼ ▶", "Move"),
            HintItem.KeyHint("OK", "Open"),
            HintItem.ColorHint(ColorGreen, "Sort"),
            HintItem.ColorHint(ColorYellow, "Tile size"),
            HintItem.ColorHint(ColorBlue, "Search"),
            HintItem.KeyHint("CH±", "Page"),
            HintItem.KeyHint("BACK", "Up"),
            HintItem.KeyHint("MENU", "Menu")
        )
        else -> listOf(
            HintItem.KeyHint("◀ ▲ ▼ ▶", "Move"),
            HintItem.KeyHint("OK", "Open"),
            HintItem.ColorHint(ColorRed, "Favorite"),
            HintItem.ColorHint(ColorGreen, "Sort"),
            HintItem.ColorHint(ColorYellow, "Tile size"),
            HintItem.ColorHint(ColorBlue, "Search"),
            HintItem.KeyHint("CH±", "Page"),
            HintItem.KeyHint("BACK", "Up"),
            HintItem.KeyHint("MENU", "Menu")
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(HintBarBgColor)
            .border(d.px(1f), LineColor)
            .padding(horizontal = d.rem(1f), vertical = d.rem(0.5f)),
        contentAlignment = Alignment.Center
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(d.rem(1.1f), Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(d.rem(0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            hints.forEach { hint ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.rem(0.4f))
                ) {
                    when (hint) {
                        is HintItem.KeyHint -> {
                            val chipShape = RoundedCornerShape(d.rem(0.4f))
                            Box(
                                modifier = Modifier
                                    .widthIn(min = d.rem(1.6f))
                                    .height(d.rem(1.35f))
                                    .clip(chipShape)
                                    .background(S3Color)
                                    .border(d.px(1f), S4Color, chipShape)
                                    .padding(horizontal = d.rem(0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = hint.key,
                                    color = FgColor,
                                    fontSize = d.sp(0.66f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = hint.label,
                                color = SubColor,
                                fontSize = d.sp(0.74f)
                            )
                        }
                        is HintItem.ColorHint -> {
                            val colorShape = RoundedCornerShape(d.rem(0.3f))
                            Box(
                                modifier = Modifier
                                    .size(d.rem(0.95f))
                                    .clip(colorShape)
                                    .background(hint.color)
                            )
                            Text(
                                text = hint.label,
                                color = SubColor,
                                fontSize = d.sp(0.74f)
                            )
                        }
                    }
                }
            }
        }
    }
}
