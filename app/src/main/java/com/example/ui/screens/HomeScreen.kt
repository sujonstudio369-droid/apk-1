package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Device
import com.example.data.Formatters
import com.example.ui.components.DeviceCard
import com.example.ui.components.FavoritesTile
import com.example.ui.theme.FgColor
import com.example.ui.theme.LocalDimens
import com.example.ui.theme.MuteColor
import com.example.ui.theme.SubColor

@Composable
fun HomeScreen(
    devices: List<Device>,
    favoritesCount: Int,
    onDeviceClick: (Device) -> Unit,
    onDeviceOptionsClick: (Device) -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val d = LocalDimens.current

    val onlineDevices = devices.filter { it.connected }
    val totalFolders = onlineDevices.sumOf { it.folders.size }
    val totalPhotos = onlineDevices.sumOf { it.photoCount }

    val summaryText = "${Formatters.plural(onlineDevices.size, "device")} connected · ${Formatters.plural(totalFolders, "folder")} · ${Formatters.plural(totalPhotos, "photo")}"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = d.rem(2f), vertical = d.rem(3f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = d.rem(64f))
                .fillMaxWidth()
        ) {
            // Heading Block
            Column(modifier = Modifier.padding(bottom = d.rem(2f))) {
                Text(
                    text = "STORAGE",
                    color = MuteColor,
                    fontSize = d.sp(0.75f),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = d.sp(0.2f),
                    modifier = Modifier.padding(bottom = d.rem(0.5f))
                )
                Text(
                    text = "Choose a device",
                    color = FgColor,
                    fontSize = d.sp(2.25f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = summaryText,
                    color = SubColor,
                    fontSize = d.sp(1f),
                    modifier = Modifier.padding(top = d.rem(0.5f))
                )
            }

            // Device Cards (2 columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(d.rem(1.25f))
            ) {
                onlineDevices.forEach { dev ->
                    DeviceCard(
                        device = dev,
                        onClick = { onDeviceClick(dev) },
                        onOptionsClick = { onDeviceOptionsClick(dev) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(d.rem(1.25f)))

            // Favorites Tile
            FavoritesTile(
                photoCount = favoritesCount,
                onClick = onFavoritesClick
            )
        }
    }
}
