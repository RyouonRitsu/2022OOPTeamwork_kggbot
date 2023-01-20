package org.ritsu.mirai.plugin.entity

import java.awt.Graphics

/**
 * @author wcy
 */
class MyDrawText {
    companion object {
        fun drawStringWithFontStyleLineFeed(
            g: Graphics,
            strContent: String,
            locX: Int,
            Y: Int,
            rowWidth: Int
        ): Int {
            //获取字符串 字符的总宽度
            var locY = Y
            val strWidth = getStringLength(g, strContent)
            val strHeight = getStringHeight(g)
            if (strWidth > rowWidth) {
                val rowstrnum = getRowStrNum(strContent.length, rowWidth, strWidth)
                val rows = getRows(strWidth, rowWidth)
                var temp: String
                for (i in 0 until rows) {
                    //获取各行的String
                    temp = if (i == rows - 1) {
                        //最后一行
                        strContent.substring(i * rowstrnum)
                    } else {
                        strContent.substring(i * rowstrnum, i * rowstrnum + rowstrnum)
                    }
                    //第一行不需要增加字符高度，以后的每一行在换行的时候都需要增加字符高度
                    if (i > 0) locY += strHeight
                    g.drawString(temp, locX, locY)
                }
            } else {
                //直接绘制
                g.drawString(strContent, locX, locY)
            }
            return locY
        }

        private fun getRows(strWidth: Int, rowWidth: Int): Int {
            return if (strWidth % rowWidth > 0) strWidth / rowWidth + 1 else strWidth / rowWidth
        }

        private fun getStringHeight(g: Graphics): Int {
            return g.fontMetrics.height
        }

        private fun getRowStrNum(strnum: Int, rowWidth: Int, strWidth: Int): Int {
            return (rowWidth * strnum) / strWidth
        }

        private fun getStringLength(g: Graphics, str: String): Int {
            return g.fontMetrics.stringWidth(str)
        }
    }
}
