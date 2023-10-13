package utils.image.blur

import utils.image.blur.ImageMath.mod
import java.awt.image.BufferedImage
import kotlin.math.*

/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/ /**
 *
 * from http://www.jhlabs.com/ip/blurring.html
 */
class LensBlurFilter : AbstractBufferedImageOp() {
    /**
     * Get the radius of the kernel.
     * @return the radius
     */
    /**
     * Set the radius of the kernel, and hence the amount of blur.
     * @param radius the radius of the blur in pixels.
     */
    var radius = 10f
    var bloom = 2f
    var bloomThreshold = 192f
    private val angle = 0f
    var sides = 5
    override fun filter(src: BufferedImage, dst: BufferedImage): BufferedImage {
        var dst: BufferedImage? = dst
        val width = src.width
        val height = src.height
        var rows = 1
        var cols = 1
        var log2rows = 0
        var log2cols = 0
        val iradius = ceil(radius.toDouble()).toInt()
        var tileWidth = 128
        var tileHeight = tileWidth
        val adjustedWidth = (width + iradius * 2)
        val adjustedHeight = (height + iradius * 2)
        tileWidth = if (iradius < 32) min(128.0, (width + 2 * iradius).toDouble()).toInt() else min(
            256.0,
            (width + 2 * iradius).toDouble()
        )
            .toInt()
        tileHeight = if (iradius < 32) min(128.0, (height + 2 * iradius).toDouble()).toInt() else min(
            256.0,
            (height + 2 * iradius).toDouble()
        )
            .toInt()
        if (dst == null) dst = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        while (rows < tileHeight) {
            rows *= 2
            log2rows++
        }
        while (cols < tileWidth) {
            cols *= 2
            log2cols++
        }
        val w = cols
        val h = rows
        tileWidth = w
        tileHeight = h // tileWidth, w, and cols are always all the same
        val fft = FFT(
            max(log2rows.toDouble(), log2cols.toDouble()).toInt()
        )
        val rgb = IntArray(w * h)
        val mask = Array(2) { FloatArray(w * h) }
        val gb = Array(2) { FloatArray(w * h) }
        val ar = Array(2) { FloatArray(w * h) }

        // Create the kernel
        val polyAngle = Math.PI / sides
        val polyScale = 1.0f / cos(polyAngle)
        val r2 = (radius * radius).toDouble()
        val rangle = Math.toRadians(angle.toDouble())
        var total = 0f
        var i = 0
        for (y in 0 until h) {
            for (x in 0 until w) {
                val dx = (x - w / 2f).toDouble()
                val dy = (y - h / 2f).toDouble()
                var r = dx * dx + dy * dy
                var f = (if (r < r2) 1 else 0).toDouble()
                if (f != 0.0) {
                    r = sqrt(r)
                    if (sides != 0) {
                        var a = atan2(dy, dx) + rangle
                        a = mod(a, polyAngle * 2) - polyAngle
                        f = cos(a) * polyScale
                    } else f = 1.0
                    f = (if (f * r < radius) 1 else 0).toDouble()
                }
                total += f.toFloat()
                mask[0][i] = f.toFloat()
                mask[1][i] = 0f
                i++
            }
        }

        // Normalize the kernel
        i = 0
        for (y in 0 until h) {
            for (x in 0 until w) {
                mask[0][i] /= total
                i++
            }
        }
        fft.transform2D(mask[0], mask[1], w, h, true)
        var tileY = -iradius
        while (tileY < height) {
            var tileX = -iradius
            while (tileX < width) {

//                System.out.println("Tile: "+tileX+" "+tileY+" "+tileWidth+" "+tileHeight);

                // Clip the tile to the image bounds
                var tx = tileX
                var ty = tileY
                var tw = tileWidth
                var th = tileHeight
                var fx = 0
                var fy = 0
                if (tx < 0) {
                    tw += tx
                    fx -= tx
                    tx = 0
                }
                if (ty < 0) {
                    th += ty
                    fy -= ty
                    ty = 0
                }
                if (tx + tw > width) tw = width - tx
                if (ty + th > height) th = height - ty
                src.getRGB(tx, ty, tw, th, rgb, fy * w + fx, w)

                // Create a float array from the pixels. Any pixels off the edge of the source image get duplicated from the edge.
                i = 0
                for (y in 0 until h) {
                    val imageY = y + tileY
                    var j: Int
                    j = if (imageY < 0) fy else if (imageY > height) fy + th - 1 else y
                    j *= w
                    for (x in 0 until w) {
                        val imageX = x + tileX
                        var k: Int
                        k = if (imageX < 0) fx else if (imageX > width) fx + tw - 1 else x
                        k += j
                        ar[0][i] = (rgb[k] shr 24 and 0xff).toFloat()
                        var r = (rgb[k] shr 16 and 0xff).toFloat()
                        var g = (rgb[k] shr 8 and 0xff).toFloat()
                        var b = (rgb[k] and 0xff).toFloat()

                        // Bloom...
                        if (r > bloomThreshold) r *= bloom
                        //							r = bloomThreshold + (r-bloomThreshold) * bloom;
                        if (g > bloomThreshold) g *= bloom
                        //							g = bloomThreshold + (g-bloomThreshold) * bloom;
                        if (b > bloomThreshold) b *= bloom
                        //							b = bloomThreshold + (b-bloomThreshold) * bloom;
                        ar[1][i] = r
                        gb[0][i] = g
                        gb[1][i] = b
                        i++
                        k++
                    }
                }

                // Transform into frequency space
                fft.transform2D(ar[0], ar[1], cols, rows, true)
                fft.transform2D(gb[0], gb[1], cols, rows, true)

                // Multiply the transformed pixels by the transformed kernel
                i = 0
                for (y in 0 until h) {
                    for (x in 0 until w) {
                        var re = ar[0][i]
                        var im = ar[1][i]
                        val rem = mask[0][i]
                        val imm = mask[1][i]
                        ar[0][i] = re * rem - im * imm
                        ar[1][i] = re * imm + im * rem
                        re = gb[0][i]
                        im = gb[1][i]
                        gb[0][i] = re * rem - im * imm
                        gb[1][i] = re * imm + im * rem
                        i++
                    }
                }

                // Transform back
                fft.transform2D(ar[0], ar[1], cols, rows, false)
                fft.transform2D(gb[0], gb[1], cols, rows, false)

                // Convert back to RGB pixels, with quadrant remapping
                val row_flip = w shr 1
                val col_flip = h shr 1
                var index = 0

                // don't bother converting pixels off image edges
                for (y in 0 until w) {
                    val ym = y xor row_flip
                    val yi = ym * cols
                    for (x in 0 until w) {
                        val xm = yi + (x xor col_flip)
                        val a = ar[0][xm].toInt()
                        var r = ar[1][xm].toInt()
                        var g = gb[0][xm].toInt()
                        var b = gb[1][xm].toInt()

                        // Clamp high pixels due to blooming
                        if (r > 255) r = 255
                        if (g > 255) g = 255
                        if (b > 255) b = 255
                        val argb = a shl 24 or (r shl 16) or (g shl 8) or b
                        rgb[index++] = argb
                    }
                }

                // Clip to the output image
                tx = tileX + iradius
                ty = tileY + iradius
                tw = tileWidth - 2 * iradius
                th = tileHeight - 2 * iradius
                if (tx + tw > width) tw = width - tx
                if (ty + th > height) th = height - ty
                dst.setRGB(tx, ty, tw, th, rgb, iradius * w + iradius, w)
                tileX += tileWidth - 2 * iradius
            }
            tileY += tileHeight - 2 * iradius
        }
        return dst
    }

    override fun toString(): String {
        return "Blur/Lens Blur..."
    }
}