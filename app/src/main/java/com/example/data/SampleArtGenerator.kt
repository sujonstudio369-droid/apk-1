package com.example.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object SampleArtGenerator {

    private class Mulberry32(private var a: Int) {
        fun next(): Float {
            a = a + 0x6D2B79F5.toInt()
            var t = a xor (a ushr 15)
            t = t * (1 or a)
            t = t xor (t + (t xor (t ushr 7)) * (61 or t))
            return ((t xor (t ushr 14)).toLong() and 0xFFFFFFFFL).toFloat() / 4294967296f
        }
    }

    private fun hsla(lightness: Float, alpha: Float = 1f): Int {
        val l = (lightness.coerceIn(0f, 100f) / 100f * 255f).toInt()
        val a = (alpha.coerceIn(0f, 1f) * 255f).toInt()
        return Color.argb(a, l, l, l)
    }

    fun renderArt(photo: Photo, scale: Float = 1.0f): Bitmap {
        val w = max(32, (photo.width * scale).toInt())
        val h = max(32, (photo.height * scale).toInt())
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val rng = Mulberry32(photo.seed)

        canvas.save()
        canvas.scale(scale, scale)
        val pw = photo.width.toFloat()
        val ph = photo.height.toFloat()

        when (photo.style % 6) {
            0 -> drawAlpineRidge(canvas, pw, ph, rng)
            1 -> drawDuneLight(canvas, pw, ph, rng)
            2 -> drawMoonrise(canvas, pw, ph, rng)
            3 -> drawOrbit(canvas, pw, ph, rng)
            4 -> drawSkyline(canvas, pw, ph, rng)
            else -> drawTide(canvas, pw, ph, rng)
        }

        // Vignette
        val vPaint = Paint().apply {
            shader = RadialGradient(
                pw / 2f, ph / 2f, max(pw, ph) * 0.8f,
                intArrayOf(Color.TRANSPARENT, Color.argb(115, 0, 0, 0)),
                floatArrayOf(0.4f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, pw, ph, vPaint)

        canvas.restore()
        return bmp
    }

    private fun drawAlpineRidge(canvas: Canvas, w: Float, h: Float, rng: Mulberry32) {
        val skyPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(hsla(20f), hsla(56f + rng.next() * 10f), hsla(80f)),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, skyPaint)

        val discX = w * (0.2f + rng.next() * 0.6f)
        val discY = h * (0.22f + rng.next() * 0.15f)
        val discR = h * (0.05f + rng.next() * 0.04f)
        drawGlowDisc(canvas, discX, discY, discR, 92f)

        for (i in 0 until 5) {
            drawRidge(canvas, w, h, rng, h * (0.38f + i * 0.1f), h * (0.22f - i * 0.018f), hsla(68f - i * 13f), true)
        }

        val fogPaint = Paint().apply {
            shader = LinearGradient(
                0f, h * 0.6f, 0f, h,
                intArrayOf(hsla(70f, 0f), hsla(8f, 0.8f)),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, h * 0.6f, w, h, fogPaint)
    }

    private fun drawDuneLight(canvas: Canvas, w: Float, h: Float, rng: Mulberry32) {
        val skyPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(hsla(44f), hsla(82f), hsla(92f)),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, skyPaint)

        drawGlowDisc(canvas, w * (0.15f + rng.next() * 0.7f), h * (0.3f + rng.next() * 0.1f), h * 0.06f, 97f)

        for (i in 0 until 6) {
            drawRidge(canvas, w, h, rng, h * (0.45f + i * 0.08f), h * 0.1f, hsla(78f - i * 12f), false)
        }
    }

    private fun drawMoonrise(canvas: Canvas, w: Float, h: Float, rng: Mulberry32) {
        val skyPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(hsla(4f), hsla(22f), hsla(38f)),
                floatArrayOf(0f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, skyPaint)

        val starPaint = Paint()
        for (i in 0 until 120) {
            starPaint.color = hsla(70f + rng.next() * 30f, 0.25f + rng.next() * 0.6f)
            canvas.drawCircle(rng.next() * w, rng.next() * h * 0.65f, 0.6f + rng.next() * 1.8f, starPaint)
        }

        drawGlowDisc(canvas, w * (0.25f + rng.next() * 0.5f), h * (0.25f + rng.next() * 0.12f), h * 0.07f, 90f)

        for (i in 0 until 3) {
            drawRidge(canvas, w, h, rng, h * (0.55f + i * 0.1f), h * 0.18f, hsla(18f - i * 6f), true)
        }
    }

    private fun drawOrbit(canvas: Canvas, w: Float, h: Float, rng: Mulberry32) {
        val bgPaint = Paint().apply {
            shader = RadialGradient(
                w / 2f, h / 2f, max(w, h) * 0.7f,
                intArrayOf(hsla(30f), hsla(6f)),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        val cx = w * (0.35f + rng.next() * 0.3f)
        val cy = h * (0.35f + rng.next() * 0.3f)
        val m = min(w, h)

        val strokePaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = max(1.5f, m * 0.002f)
        }
        for (i in 1..7) {
            strokePaint.color = hsla(60f, 0.12f + i * 0.03f)
            canvas.drawCircle(cx, cy, m * (0.08f + i * 0.07f), strokePaint)
        }

        val planetPaint = Paint().apply {
            shader = RadialGradient(
                cx - m * 0.03f, cy - m * 0.03f, m * 0.1f,
                intArrayOf(hsla(96f), hsla(40f)),
                null,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(cx, cy, m * 0.1f, planetPaint)
    }

    private fun drawSkyline(canvas: Canvas, w: Float, h: Float, rng: Mulberry32) {
        val skyPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(hsla(12f), hsla(52f), hsla(74f)),
                floatArrayOf(0f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, skyPaint)

        for (layer in 0 until 3) {
            val base = h * (0.78f + layer * 0.06f)
            val shade = 34f - layer * 11f
            val bPaint = Paint().apply { color = hsla(shade) }
            val wPaint = Paint()

            var x = -20f
            while (x < w) {
                val bw = w * (0.03f + rng.next() * 0.06f)
                val bh = h * (0.16f + rng.next() * 0.3f - layer * 0.03f)
                canvas.drawRect(x, base - bh, x + bw, h, bPaint)

                if (layer > 0) {
                    var wy = base - bh + 12f
                    while (wy < h - 14f) {
                        var wx = x + 8f
                        while (wx < x + bw - 8f) {
                            if (rng.next() > 0.6f) {
                                wPaint.color = hsla(82f, 0.55f + rng.next() * 0.4f)
                                canvas.drawRect(wx, wy, wx + 6f, wy + 9f, wPaint)
                            }
                            wx += 16f
                        }
                        wy += 20f
                    }
                }
                x += bw + w * 0.004f
            }
        }
    }

    private fun drawTide(canvas: Canvas, w: Float, h: Float, rng: Mulberry32) {
        val skyPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(hsla(26f), hsla(70f), hsla(62f), hsla(12f)),
                floatArrayOf(0f, 0.45f, 0.46f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, skyPaint)

        drawGlowDisc(canvas, w * (0.3f + rng.next() * 0.4f), h * 0.36f, h * 0.045f, 96f)

        val sx = w * 0.5f
        val sunReflection = Paint().apply { color = hsla(90f, 0.2f) }
        canvas.drawRect(sx - w * 0.03f, h * 0.47f, sx + w * 0.03f, h * 0.97f, sunReflection)

        for (i in 0 until 9) {
            val y0 = h * (0.48f + i * 0.06f)
            val a = h * (0.004f + i * 0.004f)
            val f = 8f + rng.next() * 6f
            val p = rng.next() * 6f

            val path = Path()
            path.moveTo(0f, h)
            var x = 0f
            while (x <= w) {
                val y = y0 + sin((x / w * f + p).toDouble()).toFloat() * a
                path.lineTo(x, y)
                x += w / 120f
            }
            path.lineTo(w, h)
            path.close()

            val wavePaint = Paint().apply { color = hsla(46f - i * 4.5f, 0.9f) }
            canvas.drawPath(path, wavePaint)
        }
    }

    private fun drawGlowDisc(canvas: Canvas, x: Float, y: Float, r: Float, l: Float) {
        val glowPaint = Paint().apply {
            shader = RadialGradient(
                x, y, r * 5f,
                intArrayOf(hsla(l, 0.55f), hsla(l, 0f)),
                floatArrayOf(0.2f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(x, y, r * 5f, glowPaint)

        val corePaint = Paint().apply { color = hsla(min(98f, l + 4f)) }
        canvas.drawCircle(x, y, r, corePaint)
    }

    private fun drawRidge(canvas: Canvas, w: Float, h: Float, rng: Mulberry32, base: Float, amp: Float, color: Int, sharp: Boolean) {
        val p = floatArrayOf(rng.next() * 6f, rng.next() * 6f, rng.next() * 6f)
        val k = floatArrayOf(2f + rng.next() * 2f, 5f + rng.next() * 3f, 11f + rng.next() * 5f)

        val path = Path()
        path.moveTo(0f, h)

        var x = 0f
        while (x <= w + 1f) {
            val t = x / w
            val y = if (sharp) {
                base + amp * (
                    0.55f * abs(sin((t * k[0] + p[0]).toDouble()).toFloat()) +
                    0.3f * abs(sin((t * k[1] + p[1]).toDouble()).toFloat()) +
                    0.15f * abs(sin((t * k[2] + p[2]).toDouble()).toFloat())
                )
            } else {
                base + amp * (
                    0.6f * sin((t * k[0] + p[0]).toDouble()).toFloat() +
                    0.3f * sin((t * k[1] + p[1]).toDouble()).toFloat() +
                    0.1f * sin((t * k[2] + p[2]).toDouble()).toFloat()
                )
            }
            path.lineTo(x, y)
            x += w / 160f
        }
        path.lineTo(w, h)
        path.close()

        val ridgePaint = Paint().apply { this.color = color }
        canvas.drawPath(path, ridgePaint)
    }
}
