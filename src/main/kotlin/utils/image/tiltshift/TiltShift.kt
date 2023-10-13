package utils.image.tiltshift

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import utils.image.blur.LensBlurFilter
import java.awt.image.BufferedImage
import kotlin.math.roundToInt


object TiltShift {
    private val filter = LensBlurFilter()


    suspend fun BufferedImage.blur(
        radius: Float,
        top: Float,
        bottom: Float,
    ): BufferedImage {
        return withContext(Dispatchers.IO) {
            val imgList = this@blur.split(top, bottom)

            val blurTop = imgList[0].blur(radius)
            val blurBottom = imgList[2].blur(radius)

            mergeImg(listOf(blurTop, imgList[1], blurBottom))
        }
    }

    private fun BufferedImage.blur(
        radius: Float,
    ): BufferedImage {
        filter.radius = radius

        val dst = BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB)

        return filter.filter(this, dst)
    }

    /**
     * @return listOf(topImg, normalImg, bottomImg)
     * */
    private fun BufferedImage.split(
        top: Float,
        bottom: Float
    ): List<BufferedImage> {
        val topHeight = this.height * top
        val bottomHeight = this.height * bottom

        val topImg = BufferedImage(this.width, topHeight.roundToInt(), BufferedImage.TYPE_INT_RGB)
        val bottomImg = BufferedImage(this.width, bottomHeight.roundToInt(), BufferedImage.TYPE_INT_RGB)
        val normalImg = BufferedImage(this.width, this.height - topImg.height - bottomImg.height, BufferedImage.TYPE_INT_RGB)

        val topGraphics2D = topImg.createGraphics()
        topGraphics2D.drawImage(this,
            0, 0, topImg.width, topImg.height,
            0, 0, this.width, topImg.height,
            null)
        topGraphics2D.dispose()

        val bottomGraphics2D = bottomImg.createGraphics()
        bottomGraphics2D.drawImage(this,
            0, 0, bottomImg.width, bottomImg.height,
            0, (this.height * (1f - bottom)).roundToInt(), bottomImg.width, this.height,
            null)
        bottomGraphics2D.dispose()

        val normalGraphics2D = normalImg.createGraphics()
        normalGraphics2D.drawImage(this,
            0, 0, normalImg.width, normalImg.height,
            0, topImg.height, normalImg.width, (this.height * (1f - bottom)).roundToInt(),
            null)
        normalGraphics2D.dispose()

        return listOf(topImg, normalImg, bottomImg)
    }

    private fun mergeImg(
        imgList: List<BufferedImage>
    ): BufferedImage {
        val totalHeight = imgList.sumOf {
            it.height
        }

        val resultImg = BufferedImage(imgList[0].width, totalHeight, BufferedImage.TYPE_INT_RGB)
        val topGraphics2D = resultImg.createGraphics()

        var currentHeight = 0
        imgList.forEach {
            topGraphics2D.drawImage(it, 0, currentHeight, it.width, it.height, null)
            currentHeight += it.height
        }

        topGraphics2D.dispose()

        return resultImg
    }
}