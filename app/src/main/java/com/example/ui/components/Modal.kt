package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.Device
import com.example.data.DeviceType
import com.example.data.Formatters
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
import com.example.ui.theme.ModalScrimColor
import com.example.ui.theme.MuteColor
import com.example.ui.theme.S1Color
import com.example.ui.theme.S2Color
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor

@Composable
fun DevicePropertiesModal(
    device: Device,
    onClose: () -> Unit
) {
    val d = LocalDimens.current
    val pct = if (device.totalBytes > 0) {
        ((device.usedBytes.toDouble() / device.totalBytes) * 100).toInt()
    } else 0
    val freeBytes = maxOf(0L, device.totalBytes - device.usedBytes)

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ModalScrimColor)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            val cardShape = RoundedCornerShape(d.rem(1.6f))
            Column(
                modifier = Modifier
                    .widthIn(min = d.rem(26f), max = d.rem(28f))
                    .clickable(enabled = false) {}
                    .shadow(30.dp, cardShape)
                    .clip(cardShape)
                    .background(S2Color)
                    .border(BorderStroke(d.px(1f), S4Color), cardShape)
                    .padding(d.rem(1.5f))
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = d.rem(1.25f)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.rem(1f))
                ) {
                    val iconShape = RoundedCornerShape(d.rem(1f))
                    Box(
                        modifier = Modifier
                            .size(d.rem(3.5f))
                            .clip(iconShape)
                            .background(S1Color)
                            .border(BorderStroke(d.px(1f), LineColor), iconShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(
                            icon = if (device.type == DeviceType.USB) AppIconType.USB else AppIconType.SD,
                            tint = AccColor,
                            size = d.rem(1.75f)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = device.name,
                            color = FgColor,
                            fontSize = d.sp(1.125f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (device.type == DeviceType.USB) "Removable USB drive" else "Internal storage",
                            color = MuteColor,
                            fontSize = d.sp(0.875f)
                        )
                    }

                    BtnIcon(
                        icon = AppIconType.X,
                        onClick = onClose,
                        contentDescription = "Close"
                    )
                }

                // Usage bar
                UsageBar(percentage = pct.toFloat(), height = 6.dp)

                Spacer(modifier = Modifier.height(d.rem(0.5f)))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = d.rem(1.25f)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "$pct% used", color = MuteColor, fontSize = d.sp(0.75f))
                    Text(text = "${Formatters.fmtGB(freeBytes)} free", color = MuteColor, fontSize = d.sp(0.75f))
                }

                // Definition list
                val rows = listOf(
                    Pair("Used", Formatters.fmtGB(device.usedBytes)),
                    Pair("Capacity", Formatters.fmtGB(device.totalBytes)),
                    Pair("Folders", device.folders.size.toString()),
                    Pair("Photos", device.photoCount.toString()),
                    Pair("Status", if (device.connected) "Connected" else "Disconnected")
                )

                Column(verticalArrangement = Arrangement.spacedBy(d.rem(0.625f))) {
                    rows.forEach { (label, value) ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = label,
                                color = MuteColor,
                                fontSize = d.sp(0.875f),
                                modifier = Modifier.width(d.rem(7f))
                            )
                            Text(
                                text = value,
                                color = FgColor,
                                fontSize = d.sp(0.875f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShortcutsModal(onClose: () -> Unit) {
    val d = LocalDimens.current

    val remoteList = listOf(
        Pair("◀ ▲ ▼ ▶", "Move focus · pan a zoomed photo"),
        Pair("OK", "Open · select · zoom photo"),
        Pair("BACK / EXIT", "Go back · close"),
        Pair("MENU", "Open / close menu"),
        Pair("INFO", "Details · device options"),
        Pair("color_red", "Favorite"),
        Pair("color_green", "Slideshow · sort"),
        Pair("color_yellow", "Rotate · tile size"),
        Pair("color_blue", "Details · search"),
        Pair("CH + / −", "Next / previous photo · page"),
        Pair("⏮ ⏭ ⏪ ⏩", "Previous / next photo (skip 5)"),
        Pair("▶ ❚❚ ■", "Slideshow play / pause / stop"),
        Pair("1 / 3", "Zoom out / in"),
        Pair("4 / 6", "Previous / next photo"),
        Pair("2 / 8", "Pan up / down"),
        Pair("5 or 0", "Fit to screen"),
        Pair("7", "Rotate"),
        Pair("9", "Slideshow"),
        Pair("1 – 9 (in lists)", "Jump to item")
    )

    val keyList = listOf(
        Pair("← → ↑ ↓", "Move focus · photos"),
        Pair("Enter", "Open · select"),
        Pair("+ −", "Zoom in / out"),
        Pair("0", "Reset zoom"),
        Pair("R", "Rotate"),
        Pair("Space", "Slideshow"),
        Pair("F", "Full screen"),
        Pair("I", "Photo details"),
        Pair("H", "Favorite"),
        Pair("T", "Show / hide thumbnails"),
        Pair("/", "Search"),
        Pair("?", "Shortcuts"),
        Pair("Esc", "Close")
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ModalScrimColor)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            val cardShape = RoundedCornerShape(d.rem(1.6f))
            Column(
                modifier = Modifier
                    .widthIn(min = d.rem(22f), max = d.rem(26f))
                    .clickable(enabled = false) {}
                    .shadow(30.dp, cardShape)
                    .clip(cardShape)
                    .background(S2Color)
                    .border(BorderStroke(d.px(1f), S4Color), cardShape)
                    .padding(d.rem(1.25f))
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = d.rem(0.75f)),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(d.rem(0.75f))
                ) {
                    val iconShape = RoundedCornerShape(d.rem(0.7f))
                    Box(
                        modifier = Modifier
                            .size(d.rem(2.25f))
                            .clip(iconShape)
                            .background(S1Color)
                            .border(BorderStroke(d.px(1f), LineColor), iconShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AppIcon(icon = AppIconType.KEYBOARD, tint = AccColor, size = d.rem(1.25f))
                    }

                    Text(
                        text = "Remote & keyboard",
                        color = FgColor,
                        fontSize = d.sp(1f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    BtnIcon(
                        icon = AppIconType.X,
                        onClick = onClose,
                        contentDescription = "Close"
                    )
                }

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .heightIn(max = d.rem(18f))
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "TV REMOTE",
                        color = MuteColor,
                        fontSize = d.sp(0.68f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = d.rem(0.5f))
                    )

                    remoteList.forEach { (key, desc) ->
                        ShortcutRow(key = key, description = desc)
                    }

                    Text(
                        text = "KEYBOARD",
                        color = MuteColor,
                        fontSize = d.sp(0.68f),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = d.rem(0.9f), bottom = d.rem(0.5f))
                    )

                    keyList.forEach { (key, desc) ->
                        ShortcutRow(key = key, description = desc)
                    }
                }
            }
        }
    }
}

@Composable
fun ShortcutRow(key: String, description: String) {
    val d = LocalDimens.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(d.px(1f), LineColor))
            .padding(vertical = d.rem(0.55f), horizontal = d.rem(0.25f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        when (key) {
            "color_red" -> ColorKeyBadge(ColorRed, "Red")
            "color_green" -> ColorKeyBadge(ColorGreen, "Green")
            "color_yellow" -> ColorKeyBadge(ColorYellow, "Yellow")
            "color_blue" -> ColorKeyBadge(ColorBlue, "Blue")
            else -> {
                val chipShape = RoundedCornerShape(d.rem(0.4f))
                Box(
                    modifier = Modifier
                        .clip(chipShape)
                        .background(S3Color)
                        .border(BorderStroke(d.px(1f), S4Color), chipShape)
                        .padding(horizontal = d.rem(0.4f), vertical = d.rem(0.1f))
                ) {
                    Text(
                        text = key,
                        color = FgColor,
                        fontSize = d.sp(0.7f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Text(
            text = description,
            color = SubColor,
            fontSize = d.sp(0.85f)
        )
    }
}

@Composable
fun ColorKeyBadge(color: Color, label: String) {
    val d = LocalDimens.current
    val chipShape = RoundedCornerShape(d.rem(0.4f))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.rem(0.3f)),
        modifier = Modifier
            .clip(chipShape)
            .background(S3Color)
            .border(BorderStroke(d.px(1f), S4Color), chipShape)
            .padding(horizontal = d.rem(0.4f), vertical = d.rem(0.1f))
    ) {
        Box(
            modifier = Modifier
                .size(d.rem(0.7f))
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = label,
            color = FgColor,
            fontSize = d.sp(0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}
