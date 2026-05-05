package com.mrsep.musicrecognizer.feature.track.presentation.track

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.net.URL

internal fun createAndShareStoryCard(
    context: Context,
    track: TrackUi,
) {
    Thread {
        try {
            val cardWidth = 1080
            val cardHeight = 1920
            val bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Background color from theme seed or default purple
            val bgColor = track.themeSeedColor ?: Color.parseColor("#1A1A2E")
            canvas.drawColor(bgColor)

            // Draw gradient overlay
            val gradientPaint = Paint()
            val gradient = LinearGradient(
                0f, 0f, 0f, cardHeight.toFloat(),
                intArrayOf(
                    Color.argb(0, 0, 0, 0),
                    Color.argb(180, 0, 0, 0),
                    Color.argb(240, 0, 0, 0)
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            gradientPaint.shader = gradient
            canvas.drawRect(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(), gradientPaint)

            // Draw album art
            track.artworkUrl?.let { url ->
                try {
                    val artBitmap = BitmapFactory.decodeStream(URL(url).openStream())
                    val artSize = cardWidth - 120
                    val artTop = 200f
                    val scaledArt = Bitmap.createScaledBitmap(artBitmap, artSize, artSize, true)
                    val artPaint = Paint().apply { isAntiAlias = true }
                    val artRect = RectF(60f, artTop, 60f + artSize, artTop + artSize)
                    canvas.drawBitmap(scaledArt, null, artRect, artPaint)
                } catch (e: Exception) {
                    // Draw placeholder if art fails
                    val placeholderPaint = Paint().apply {
                        color = Color.argb(80, 255, 255, 255)
                        isAntiAlias = true
                    }
                    canvas.drawRoundRect(
                        RectF(60f, 200f, cardWidth - 60f, 1160f),
                        32f, 32f, placeholderPaint
                    )
                }
            }

            // NexPay-style pill at top
            val pillPaint = Paint().apply {
                color = Color.argb(180, 255, 255, 255)
                isAntiAlias = true
            }
            canvas.drawRoundRect(
                RectF(cardWidth / 2f - 120f, 80f, cardWidth / 2f + 120f, 130f),
                25f, 25f, pillPaint
            )
            val pillTextPaint = Paint().apply {
                color = Color.BLACK
                textSize = 32f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("🎵 Audile", cardWidth / 2f, 116f, pillTextPaint)

            // Song title
            val titlePaint = Paint().apply {
                color = Color.WHITE
                textSize = 96f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            drawMultilineText(canvas, track.title, titlePaint, 60f, 1280f, cardWidth - 120f)

            // Artist name
            val artistPaint = Paint().apply {
                color = Color.argb(200, 255, 255, 255)
                textSize = 64f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }
            drawMultilineText(canvas, track.artist, artistPaint, 60f, 1420f, cardWidth - 120f)

            // Album if available
            track.album?.let { album ->
                val albumPaint = Paint().apply {
                    color = Color.argb(150, 255, 255, 255)
                    textSize = 48f
                    isAntiAlias = true
                }
                canvas.drawText(album, 60f, 1520f, albumPaint)
            }

            // Bottom watermark
            val watermarkPaint = Paint().apply {
                color = Color.argb(120, 255, 255, 255)
                textSize = 36f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(
                "Recognized with Audile",
                cardWidth / 2f,
                1820f,
                watermarkPaint
            )

            // Save to cache and share
            val cachePath = File(context.cacheDir, "shared_images")
            cachePath.mkdirs()
            val file = File(cachePath, "audile_share.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "🎵 ${track.title} - ${track.artist}\nRecognized with Audile"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(shareIntent, "Share track card")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}

private fun drawMultilineText(
    canvas: Canvas,
    text: String,
    paint: Paint,
    x: Float,
    y: Float,
    maxWidth: Float
) {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = ""

    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        if (paint.measureText(testLine) <= maxWidth) {
            currentLine = testLine
        } else {
            if (currentLine.isNotEmpty()) lines.add(currentLine)
            currentLine = word
        }
    }
    if (currentLine.isNotEmpty()) lines.add(currentLine)

    lines.forEachIndexed { index, line ->
        canvas.drawText(line, x, y + (index * (paint.textSize + 12f)), paint)
    }
}