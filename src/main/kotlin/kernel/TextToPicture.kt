package org.ritsu.mirai.plugin.kernel

import org.ritsu.mirai.plugin.entity.MyDrawText
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset
import javax.imageio.ImageIO

/**
 * @author wcy
 * @param str 图片内容
 * @param font 字体
 * @param outFile 输出文件路径
 * @param bgImageFile 背景图片文件路径
 */
fun textToPicture(str: String, font: Font, outFile: File, bgImageFile: File) {
    // 读取背景图
    val bgImage = ImageIO.read(bgImageFile)
    val width = bgImage.getWidth(null)
    val height = bgImage.getHeight(null)
    // 创建图片
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_BGR)
    val g = image.graphics
    g.setClip(0, 0, width, height)
    g.drawImage(bgImage, 0, 0, width, height, null)
    g.color = Color.black
    g.font = font
    val drawText: MyDrawText.Companion = MyDrawText
    val br = BufferedReader(
        InputStreamReader(
            ByteArrayInputStream(str.toByteArray()),
            Charset.forName("utf8")
        )
    )
    var line = br.readLine()
    val locX = 60
    var locY = 200
    while (line != null) {
        locY =
            drawText.drawStringWithFontStyleLineFeed(
                g,
                line,
                locX,
                locY,
                width - g.fontMetrics.charWidth(' ') - 2 * locX
            )
        locY += g.fontMetrics.height + 15
        line = br.readLine()
    }
    g.dispose()
    // 输出png图片
    ImageIO.write(image, "png", outFile)
}