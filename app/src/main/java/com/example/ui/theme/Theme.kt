package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = AccColor,
    onPrimary = AccInkColor,
    secondary = S3Color,
    onSecondary = FgColor,
    tertiary = SubColor,
    background = BgColor,
    onBackground = FgColor,
    surface = S1Color,
    onSurface = FgColor,
    surfaceVariant = S2Color,
    onSurfaceVariant = SubColor,
    outline = LineColor,
    outlineVariant = S4Color
)

@Composable
fun PhotoViewerTheme(
    tvMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    val dimens = Dimens(screenWidthPx, density, tvMode)

    CompositionLocalProvider(
        LocalDimens provides dimens
    ) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
