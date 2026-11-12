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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.data.Device
import com.example.data.DeviceType
import com.example.ui.theme.AppIcon
import com.example.ui.theme.AppIconType
import com.example.ui.theme.FgColor
import com.example.ui.theme.LineColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.S2Color
import com.example.ui.theme.S3Color
import com.example.ui.theme.S4Color
import com.example.ui.theme.SubColor

@Composable
fun SortPopover(
    currentSort: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val d = LocalDimens.current
    val sorts = listOf(
        Pair("name-asc", "Name A–Z"),
        Pair("name-desc", "Name Z–A"),
        Pair("new", "Newest"),
        Pair("old", "Oldest"),
        Pair("big", "Largest"),
        Pair("small", "Smallest")
    )

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        val shape = RoundedCornerShape(d.rem(1.1f))
        Column(
            modifier = Modifier
                .widthIn(min = d.rem(13f))
                .shadow(24.dp, shape)
                .clip(shape)
                .background(S2Color)
                .border(BorderStroke(d.px(1f), S4Color), shape)
                .padding(d.rem(0.4f)),
            verticalArrangement = Arrangement.spacedBy(d.rem(0.1f))
        ) {
            sorts.forEach { (key, label) ->
                PopoverItem(
                    label = label,
                    isSelected = key == currentSort,
                    onClick = { onSortSelected(key) }
                )
            }
        }
    }
}

@Composable
fun DeviceOptionsPopover(
    device: Device,
    onOpen: () -> Unit,
    onProperties: () -> Unit,
    onEject: () -> Unit,
    onScan: () -> Unit,
    onDismiss: () -> Unit
) {
    val d = LocalDimens.current

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        val shape = RoundedCornerShape(d.rem(1.1f))
        Column(
            modifier = Modifier
                .widthIn(min = d.rem(13f))
                .shadow(24.dp, shape)
                .clip(shape)
                .background(S2Color)
                .border(BorderStroke(d.px(1f), S4Color), shape)
                .padding(d.rem(0.4f)),
            verticalArrangement = Arrangement.spacedBy(d.rem(0.1f))
        ) {
            PopoverItem(
                icon = AppIconType.FOLDER_OPEN,
                label = "Open",
                onClick = onOpen
            )
            PopoverItem(
                icon = AppIconType.INFO,
                label = "Properties",
                onClick = onProperties
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = d.rem(0.35f), horizontal = d.rem(0.25f)),
                thickness = d.px(1f),
                color = LineColor
            )

            if (device.type == DeviceType.USB) {
                PopoverItem(
                    icon = AppIconType.POWER,
                    label = "Safely eject",
                    onClick = onEject
                )
            } else {
                PopoverItem(
                    icon = AppIconType.REFRESH,
                    label = "Scan for devices",
                    onClick = onScan
                )
            }
        }
    }
}

@Composable
fun PopoverItem(
    label: String,
    onClick: () -> Unit,
    icon: AppIconType? = null,
    isSelected: Boolean = false
) {
    val d = LocalDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val itemShape = RoundedCornerShape(d.rem(0.75f))
    val bgColor = if (isFocused || isSelected) S3Color else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .background(bgColor)
            .focusable(true, interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
            .padding(horizontal = d.rem(0.75f), vertical = d.rem(0.6f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(d.rem(0.75f))
    ) {
        if (icon != null) {
            AppIcon(
                icon = icon,
                tint = if (isFocused) FgColor else SubColor,
                size = d.rem(1.25f)
            )
        }

        Text(
            text = label,
            color = if (isFocused || isSelected) FgColor else SubColor,
            fontSize = d.sp(0.875f),
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            AppIcon(
                icon = AppIconType.CHECK,
                tint = FgColor,
                size = d.rem(1.25f)
            )
        }
    }
}
