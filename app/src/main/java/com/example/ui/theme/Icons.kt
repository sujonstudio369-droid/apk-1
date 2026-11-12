package com.example.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppIconType {
    MENU,
    ELLIPSIS,
    FOLDER,
    FOLDER_OPEN,
    IMAGE,
    ZOOM_IN,
    ZOOM_OUT,
    ROTATE_CW,
    CHEVRON_LEFT,
    CHEVRON_RIGHT,
    CHEVRON_UP,
    CHEVRON_DOWN,
    X,
    DOWNLOAD,
    INFO,
    MAXIMIZE,
    MINIMIZE,
    PLAY,
    PAUSE,
    HEART,
    SEARCH,
    HOME,
    CHECK,
    SORT,
    POWER,
    REFRESH,
    KEYBOARD,
    SD,
    USB,
    TV
}

@Composable
fun AppIcon(
    icon: AppIconType,
    modifier: Modifier = Modifier,
    tint: Color = FgColor,
    size: Dp = 24.dp,
    strokeWidth: Float = 1.75f,
    isFilled: Boolean = false
) {
    Canvas(modifier = modifier.size(size)) {
        val scale = this.size.width / 24f
        val stroke = Stroke(
            width = strokeWidth * scale,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        fun p(x: Float, y: Float) = Offset(x * scale, y * scale)

        when (icon) {
            AppIconType.MENU -> {
                drawLine(tint, p(4f, 12f), p(20f, 12f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(4f, 6f), p(20f, 6f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(4f, 18f), p(20f, 18f), stroke.width, StrokeCap.Round)
            }
            AppIconType.ELLIPSIS -> {
                drawCircle(tint, 1.25f * scale, p(12f, 12f))
                drawCircle(tint, 1.25f * scale, p(19f, 12f))
                drawCircle(tint, 1.25f * scale, p(5f, 12f))
            }
            AppIconType.FOLDER -> {
                val path = Path().apply {
                    moveTo(4f * scale, 5f * scale)
                    lineTo(7.93f * scale, 5f * scale)
                    lineTo(9.6f * scale, 7.5f * scale)
                    lineTo(20f * scale, 7.5f * scale)
                    quadraticBezierTo(22f * scale, 7.5f * scale, 22f * scale, 9.5f * scale)
                    lineTo(22f * scale, 18f * scale)
                    quadraticBezierTo(22f * scale, 20f * scale, 20f * scale, 20f * scale)
                    lineTo(4f * scale, 20f * scale)
                    quadraticBezierTo(2f * scale, 20f * scale, 2f * scale, 18f * scale)
                    lineTo(2f * scale, 7f * scale)
                    quadraticBezierTo(2f * scale, 5f * scale, 4f * scale, 5f * scale)
                    close()
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.FOLDER_OPEN -> {
                val path = Path().apply {
                    moveTo(2f * scale, 7f * scale)
                    lineTo(2f * scale, 5f * scale)
                    quadraticBezierTo(2f * scale, 3f * scale, 4f * scale, 3f * scale)
                    lineTo(7.9f * scale, 3f * scale)
                    lineTo(9.6f * scale, 5.5f * scale)
                    lineTo(18f * scale, 5.5f * scale)
                    quadraticBezierTo(20f * scale, 5.5f * scale, 20f * scale, 7.5f * scale)
                    lineTo(20f * scale, 10f * scale)
                }
                drawPath(path, tint, style = stroke)
                val front = Path().apply {
                    moveTo(4f * scale, 20f * scale)
                    lineTo(2f * scale, 20f * scale)
                    lineTo(6f * scale, 14f * scale)
                    lineTo(7.5f * scale, 11f * scale)
                    lineTo(20f * scale, 11f * scale)
                    lineTo(18.5f * scale, 17f * scale)
                    quadraticBezierTo(18.2f * scale, 20f * scale, 16.5f * scale, 20f * scale)
                    close()
                }
                drawPath(front, tint, style = stroke)
            }
            AppIconType.IMAGE -> {
                val rr = RoundRect(3f * scale, 3f * scale, 21f * scale, 21f * scale, CornerRadius(2f * scale, 2f * scale))
                drawPath(Path().apply { addRoundRect(rr) }, tint, style = stroke)
                drawCircle(tint, 2f * scale, p(9f, 9f), style = stroke)
                val path = Path().apply {
                    moveTo(21f * scale, 15f * scale)
                    lineTo(17.9f * scale, 11.9f * scale)
                    lineTo(15.1f * scale, 14.7f * scale)
                    lineTo(6f * scale, 21f * scale)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.ZOOM_IN -> {
                drawCircle(tint, 8f * scale, p(11f, 11f), style = stroke)
                drawLine(tint, p(21f, 21f), p(16.65f, 16.65f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(11f, 8f), p(11f, 14f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(8f, 11f), p(14f, 11f), stroke.width, StrokeCap.Round)
            }
            AppIconType.ZOOM_OUT -> {
                drawCircle(tint, 8f * scale, p(11f, 11f), style = stroke)
                drawLine(tint, p(21f, 21f), p(16.65f, 16.65f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(8f, 11f), p(14f, 11f), stroke.width, StrokeCap.Round)
            }
            AppIconType.ROTATE_CW -> {
                val path = Path().apply {
                    moveTo(21f * scale, 12f * scale)
                    arcTo(
                        rect = Rect(3f * scale, 3f * scale, 21f * scale, 21f * scale),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 270f,
                        forceMoveTo = false
                    )
                    cubicTo(
                        14.52f * scale, 3f * scale,
                        16.93f * scale, 4f * scale,
                        18.74f * scale, 5.74f * scale
                    )
                    lineTo(21f * scale, 8f * scale)
                }
                drawPath(path, tint, style = stroke)

                val arrow = Path().apply {
                    moveTo(21f * scale, 3f * scale)
                    lineTo(21f * scale, 8f * scale)
                    lineTo(16f * scale, 8f * scale)
                }
                drawPath(arrow, tint, style = stroke)
            }
            AppIconType.CHEVRON_LEFT -> {
                val path = Path().apply {
                    moveTo(15f * scale, 18f * scale)
                    lineTo(9f * scale, 12f * scale)
                    lineTo(15f * scale, 6f * scale)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.CHEVRON_RIGHT -> {
                val path = Path().apply {
                    moveTo(9f * scale, 18f * scale)
                    lineTo(15f * scale, 12f * scale)
                    lineTo(9f * scale, 6f * scale)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.CHEVRON_UP -> {
                val path = Path().apply {
                    moveTo(18f * scale, 15f * scale)
                    lineTo(12f * scale, 9f * scale)
                    lineTo(6f * scale, 15f * scale)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.CHEVRON_DOWN -> {
                val path = Path().apply {
                    moveTo(6f * scale, 9f * scale)
                    lineTo(12f * scale, 15f * scale)
                    lineTo(18f * scale, 9f * scale)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.X -> {
                drawLine(tint, p(18f, 6f), p(6f, 18f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(6f, 6f), p(18f, 18f), stroke.width, StrokeCap.Round)
            }
            AppIconType.DOWNLOAD -> {
                val base = Path().apply {
                    moveTo(21f * scale, 15f * scale)
                    lineTo(21f * scale, 19f * scale)
                    quadraticBezierTo(21f * scale, 21f * scale, 19f * scale, 21f * scale)
                    lineTo(5f * scale, 21f * scale)
                    quadraticBezierTo(3f * scale, 21f * scale, 3f * scale, 19f * scale)
                    lineTo(3f * scale, 15f * scale)
                }
                drawPath(base, tint, style = stroke)
                val arrow = Path().apply {
                    moveTo(7f * scale, 10f * scale)
                    lineTo(12f * scale, 15f * scale)
                    lineTo(17f * scale, 10f * scale)
                }
                drawPath(arrow, tint, style = stroke)
                drawLine(tint, p(12f, 15f), p(12f, 3f), stroke.width, StrokeCap.Round)
            }
            AppIconType.INFO -> {
                drawCircle(tint, 10f * scale, p(12f, 12f), style = stroke)
                drawLine(tint, p(12f, 16f), p(12f, 12f), stroke.width, StrokeCap.Round)
                drawCircle(tint, 0.8f * scale, p(12f, 8f), style = Fill)
            }
            AppIconType.MAXIMIZE -> {
                val p1 = Path().apply { moveTo(8f * scale, 3f * scale); lineTo(3f * scale, 3f * scale); lineTo(3f * scale, 8f * scale) }
                val p2 = Path().apply { moveTo(21f * scale, 8f * scale); lineTo(21f * scale, 3f * scale); lineTo(16f * scale, 3f * scale) }
                val p3 = Path().apply { moveTo(3f * scale, 16f * scale); lineTo(3f * scale, 21f * scale); lineTo(8f * scale, 21f * scale) }
                val p4 = Path().apply { moveTo(16f * scale, 21f * scale); lineTo(21f * scale, 21f * scale); lineTo(21f * scale, 16f * scale) }
                drawPath(p1, tint, style = stroke)
                drawPath(p2, tint, style = stroke)
                drawPath(p3, tint, style = stroke)
                drawPath(p4, tint, style = stroke)
            }
            AppIconType.MINIMIZE -> {
                val p1 = Path().apply { moveTo(8f * scale, 3f * scale); lineTo(8f * scale, 8f * scale); lineTo(3f * scale, 8f * scale) }
                val p2 = Path().apply { moveTo(21f * scale, 8f * scale); lineTo(16f * scale, 8f * scale); lineTo(16f * scale, 3f * scale) }
                val p3 = Path().apply { moveTo(3f * scale, 16f * scale); lineTo(8f * scale, 16f * scale); lineTo(8f * scale, 21f * scale) }
                val p4 = Path().apply { moveTo(16f * scale, 21f * scale); lineTo(16f * scale, 16f * scale); lineTo(21f * scale, 16f * scale) }
                drawPath(p1, tint, style = stroke)
                drawPath(p2, tint, style = stroke)
                drawPath(p3, tint, style = stroke)
                drawPath(p4, tint, style = stroke)
            }
            AppIconType.PLAY -> {
                val path = Path().apply {
                    moveTo(6f * scale, 3f * scale)
                    lineTo(20f * scale, 12f * scale)
                    lineTo(6f * scale, 21f * scale)
                    close()
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.PAUSE -> {
                drawRoundRect(tint, p(6f, 4f), Size(4f * scale, 16f * scale), CornerRadius(1f * scale, 1f * scale), style = stroke)
                drawRoundRect(tint, p(14f, 4f), Size(4f * scale, 16f * scale), CornerRadius(1f * scale, 1f * scale), style = stroke)
            }
            AppIconType.HEART -> {
                val path = Path().apply {
                    moveTo(12f * scale, 21f * scale)
                    lineTo(5f * scale, 14f * scale)
                    cubicTo(2f * scale, 11f * scale, 2f * scale, 7f * scale, 4.5f * scale, 4.5f * scale)
                    cubicTo(7f * scale, 2f * scale, 10.5f * scale, 3f * scale, 12f * scale, 5.5f * scale)
                    cubicTo(13.5f * scale, 3f * scale, 17f * scale, 2f * scale, 19.5f * scale, 4.5f * scale)
                    cubicTo(22f * scale, 7f * scale, 22f * scale, 11f * scale, 19f * scale, 14f * scale)
                    close()
                }
                if (isFilled) {
                    drawPath(path, tint, style = Fill)
                } else {
                    drawPath(path, tint, style = stroke)
                }
            }
            AppIconType.SEARCH -> {
                drawCircle(tint, 8f * scale, p(11f, 11f), style = stroke)
                drawLine(tint, p(21f, 21f), p(16.7f, 16.7f), stroke.width, StrokeCap.Round)
            }
            AppIconType.HOME -> {
                val path = Path().apply {
                    moveTo(3f * scale, 9f * scale)
                    lineTo(12f * scale, 2f * scale)
                    lineTo(21f * scale, 9f * scale)
                    lineTo(21f * scale, 20f * scale)
                    quadraticBezierTo(21f * scale, 22f * scale, 19f * scale, 22f * scale)
                    lineTo(5f * scale, 22f * scale)
                    quadraticBezierTo(3f * scale, 22f * scale, 3f * scale, 20f * scale)
                    close()
                }
                drawPath(path, tint, style = stroke)
                val door = Path().apply {
                    moveTo(9f * scale, 22f * scale)
                    lineTo(9f * scale, 12f * scale)
                    lineTo(15f * scale, 12f * scale)
                    lineTo(15f * scale, 22f * scale)
                }
                drawPath(door, tint, style = stroke)
            }
            AppIconType.CHECK -> {
                val path = Path().apply {
                    moveTo(4f * scale, 12f * scale)
                    lineTo(9f * scale, 17f * scale)
                    lineTo(20f * scale, 6f * scale)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.SORT -> {
                val p1 = Path().apply {
                    moveTo(21f * scale, 16f * scale)
                    lineTo(17f * scale, 20f * scale)
                    lineTo(13f * scale, 16f * scale)
                }
                drawPath(p1, tint, style = stroke)
                drawLine(tint, p(17f, 20f), p(17f, 4f), stroke.width, StrokeCap.Round)

                val p2 = Path().apply {
                    moveTo(3f * scale, 8f * scale)
                    lineTo(7f * scale, 4f * scale)
                    lineTo(11f * scale, 8f * scale)
                }
                drawPath(p2, tint, style = stroke)
                drawLine(tint, p(7f, 4f), p(7f, 20f), stroke.width, StrokeCap.Round)
            }
            AppIconType.POWER -> {
                drawLine(tint, p(12f, 2f), p(12f, 12f), stroke.width, StrokeCap.Round)
                val path = Path().apply {
                    arcTo(Rect(3f * scale, 3f * scale, 21f * scale, 21f * scale), 300f, 300f, false)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconType.REFRESH -> {
                val a1 = Path().apply {
                    arcTo(Rect(3f * scale, 3f * scale, 21f * scale, 21f * scale), 200f, 130f, false)
                }
                drawPath(a1, tint, style = stroke)
                val arr1 = Path().apply {
                    moveTo(21f * scale, 3f * scale)
                    lineTo(21f * scale, 8f * scale)
                    lineTo(16f * scale, 8f * scale)
                }
                drawPath(arr1, tint, style = stroke)

                val a2 = Path().apply {
                    arcTo(Rect(3f * scale, 3f * scale, 21f * scale, 21f * scale), 20f, 130f, false)
                }
                drawPath(a2, tint, style = stroke)
                val arr2 = Path().apply {
                    moveTo(8f * scale, 16f * scale)
                    lineTo(3f * scale, 16f * scale)
                    lineTo(3f * scale, 21f * scale)
                }
                drawPath(arr2, tint, style = stroke)
            }
            AppIconType.KEYBOARD -> {
                drawRoundRect(tint, p(2f, 4f), Size(20f * scale, 16f * scale), CornerRadius(2f * scale, 2f * scale), style = stroke)
                drawLine(tint, p(7f, 16f), p(17f, 16f), stroke.width, StrokeCap.Round)
                for (x in listOf(6f, 10f, 14f, 18f)) {
                    drawCircle(tint, 0.7f * scale, p(x, 8f))
                }
                for (x in listOf(8f, 12f, 16f)) {
                    drawCircle(tint, 0.7f * scale, p(x, 12f))
                }
            }
            AppIconType.SD -> {
                val path = Path().apply {
                    moveTo(7f * scale, 21f * scale)
                    lineTo(17f * scale, 21f * scale)
                    quadraticBezierTo(19f * scale, 21f * scale, 19f * scale, 19f * scale)
                    lineTo(19f * scale, 5f * scale)
                    quadraticBezierTo(19f * scale, 3f * scale, 17f * scale, 3f * scale)
                    lineTo(11f * scale, 3f * scale)
                    lineTo(5.6f * scale, 8.4f * scale)
                    lineTo(5f * scale, 10f * scale)
                    lineTo(5f * scale, 19f * scale)
                    quadraticBezierTo(5f * scale, 21f * scale, 7f * scale, 21f * scale)
                    close()
                }
                drawPath(path, tint, style = stroke)
                drawLine(tint, p(13f, 6f), p(13f, 8f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(16f, 6f), p(16f, 8f), stroke.width, StrokeCap.Round)
                drawLine(tint, p(10f, 7f), p(10f, 8f), stroke.width, StrokeCap.Round)
            }
            AppIconType.USB -> {
                val body = Path().apply {
                    moveTo(7f * scale, 8f * scale)
                    lineTo(17f * scale, 8f * scale)
                    lineTo(17f * scale, 16f * scale)
                    arcTo(Rect(7f * scale, 11f * scale, 17f * scale, 21f * scale), 0f, 180f, false)
                    close()
                }
                drawPath(body, tint, style = stroke)
                val plug = Path().apply {
                    moveTo(9f * scale, 8f * scale)
                    lineTo(9f * scale, 3f * scale)
                    lineTo(15f * scale, 3f * scale)
                    lineTo(15f * scale, 8f * scale)
                }
                drawPath(plug, tint, style = stroke)
            }
            AppIconType.TV -> {
                drawRoundRect(tint, p(2f, 7f), Size(20f * scale, 15f * scale), CornerRadius(2f * scale, 2f * scale), style = stroke)
                val ant = Path().apply {
                    moveTo(17f * scale, 2f * scale)
                    lineTo(12f * scale, 7f * scale)
                    lineTo(7f * scale, 2f * scale)
                }
                drawPath(ant, tint, style = stroke)
            }
        }
    }
}
