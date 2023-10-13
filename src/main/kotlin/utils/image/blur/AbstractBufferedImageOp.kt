package utils.image.blur

import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.awt.image.ColorModel

/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/ /**
 * A convenience class which implements those methods of BufferedImageOp which are rarely changed.
 */
abstract class AbstractBufferedImageOp : BufferedImageOp {
    override fun createCompatibleDestImage(src: BufferedImage, dstCM: ColorModel): BufferedImage {
        var dstCM: ColorModel? = dstCM
        if (dstCM == null) dstCM = src.colorModel
        return BufferedImage(
            dstCM,
            dstCM!!.createCompatibleWritableRaster(src.width, src.height),
            dstCM.isAlphaPremultiplied,
            null
        )
    }

    override fun getBounds2D(src: BufferedImage): Rectangle2D {
        return Rectangle(0, 0, src.width, src.height)
    }

    override fun getPoint2D(srcPt: Point2D, dstPt: Point2D): Point2D {
        var dstPt: Point2D? = dstPt
        if (dstPt == null) dstPt = Point2D.Double()
        dstPt.setLocation(srcPt.x, srcPt.y)
        return dstPt
    }

    override fun getRenderingHints(): RenderingHints? {
        return null
    }

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     */
    fun getRGB(image: BufferedImage, x: Int, y: Int, width: Int, height: Int, pixels: IntArray?): IntArray {
        val type = image.type
        return if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) image.raster.getDataElements(
            x,
            y,
            width,
            height,
            pixels
        ) as IntArray else image.getRGB(
            x,
            y,
            width,
            height,
            pixels,
            0,
            width
        )
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    fun setRGB(image: BufferedImage, x: Int, y: Int, width: Int, height: Int, pixels: IntArray?) {
        val type = image.type
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) image.raster.setDataElements(
            x,
            y,
            width,
            height,
            pixels
        ) else image.setRGB(x, y, width, height, pixels, 0, width)
    }
}
