package com.example.data

import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

object Formatters {

    fun fmtBytes(bytes: Long): String {
        if (bytes < 1024L) return "$bytes B"
        var b = bytes.toDouble()
        val units = arrayOf("KB", "MB", "GB", "TB")
        var i = -1
        do {
            b /= 1024.0
            i++
        } while (b >= 1024.0 && i < 3)

        val decimals = when {
            b >= 100.0 -> 0
            b >= 10.0 -> 1
            else -> 2
        }
        val formatted = String.format(Locale.US, "%.${decimals}f", b)
        return "$formatted ${units[i]}"
    }

    fun fmtGB(bytes: Long): String {
        val gib = bytes.toDouble() / (1024.0 * 1024.0 * 1024.0)
        return fmtGB(gib)
    }

    fun fmtGB(gib: Double): String {
        val rounded = String.format(Locale.US, "%.2f", gib).trimEnd('0').trimEnd('.')
        return "$rounded GB"
    }

    fun fmtDate(timestamp: Long): String {
        if (timestamp <= 0L) return "—"
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun plural(count: Int, word: String): String {
        return "$count $word${if (count == 1) "" else "s"}"
    }

    fun normalize(s: String): String {
        val nfd = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfd).replaceAll("").lowercase(Locale.ROOT)
    }

    fun naturalCompare(a: String, b: String): Int {
        val aTokens = splitTokens(a)
        val bTokens = splitTokens(b)
        val maxLen = minOf(aTokens.size, bTokens.size)
        for (i in 0 until maxLen) {
            val aTok = aTokens[i]
            val bTok = bTokens[i]
            val aNum = aTok.toLongOrNull()
            val bNum = bTok.toLongOrNull()
            if (aNum != null && bNum != null) {
                val cmp = aNum.compareTo(bNum)
                if (cmp != 0) return cmp
            } else {
                val cmp = aTok.compareTo(bTok, ignoreCase = true)
                if (cmp != 0) return cmp
            }
        }
        return aTokens.size.compareTo(bTokens.size)
    }

    private fun splitTokens(s: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < s.length) {
            if (s[i].isDigit()) {
                val start = i
                while (i < s.length && s[i].isDigit()) i++
                tokens.add(s.substring(start, i))
            } else {
                val start = i
                while (i < s.length && !s[i].isDigit()) i++
                tokens.add(s.substring(start, i))
            }
        }
        return tokens
    }

    fun readableMime(mime: String?): String {
        val lower = mime?.lowercase(Locale.ROOT) ?: ""
        return when {
            lower.contains("jpeg") || lower.contains("jpg") -> "JPEG image"
            lower.contains("png") -> "PNG image"
            lower.contains("webp") -> "WebP image"
            lower.contains("gif") -> "GIF image"
            lower.contains("bmp") -> "BMP image"
            lower.contains("heic") || lower.contains("heif") -> "HEIC image"
            lower.contains("avif") -> "AVIF image"
            else -> "Image"
        }
    }
}
