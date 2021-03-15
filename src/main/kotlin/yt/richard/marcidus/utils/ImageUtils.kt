package yt.richard.marcidus.utils

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object ImageUtils {
    fun makeSquare(image: ByteArray): ByteArray { // renders image onto a white 1:1 square
        val top = ImageIO.read(image.inputStream())
        val size = top.width.coerceAtLeast(top.height)
        val bg = BufferedImage(size, size, top.type)
        val graphics = bg.createGraphics()
        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, bg.width, bg.height)
        graphics.drawImage(top, bg.width / 2 - top.width / 2, bg.height / 2 - top.height / 2, null)
        graphics.dispose()
        val baos = ByteArrayOutputStream()
        ImageIO.write(bg, "jpg", baos)
        return baos.use { it.toByteArray() } // return bytearray and close os
    }
}