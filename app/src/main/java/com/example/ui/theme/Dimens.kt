package com.example.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

class Dimens(val screenWidthPx: Int, val density: Density, val tvMode: Boolean) {
    private val s = maxOf(0.5f, screenWidthPx / 1920f)
    val remPx = (if (tvMode) 28.8f else 16f) * s
    val uiScale = remPx / (16f * s) // 1.8 in TV mode, 1.0 otherwise

    fun rem(v: Float): Dp = with(density) { (v * remPx).toDp() }
    fun px(v: Float): Dp = with(density) { maxOf(1f, v * s).toDp() }
    fun sp(remValue: Float): TextUnit = with(density) { (remValue * remPx / density).sp }
}

val LocalDimens = staticCompositionLocalOf<Dimens> { error("No Dimens provided") }
