package org.ritsu.mirai.plugin.entity

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class MyImage(private val pxs: Array<Array<Color>>) {
    fun height(): Int {
        return pxs.size
    }

    fun width(): Int {
        return pxs[0].size
    }

    fun getPx(y: Int, x: Int): Color {
        return pxs[y][x]
    }
}

fun loadImage(imageFile: File): MyImage =
    ImageIO.read(imageFile)
        .let {
            Array(it.height) { y ->
                Array(it.width) { x ->
                    Color(it.getRGB(x, y))
                }
            }
        }.let {
            MyImage(it)
        }

fun MyImage.save(outputFile: File): Boolean {
    return try {
        val width = width()
        val height = height()
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == 0 && (y == 0 || y == 1 || y == 2))
                    image.setRGB(x, y, 0)
                else {
                    val color = getPx(y, x)
                    image.setRGB(x, y, color.rgb)
                }
            }
        }
        ImageIO.write(image, "png", outputFile)
        true
    } catch (e: Exception) {
        println(e)
        false
    }
}