package utils.image.blur

import kotlin.math.min
import kotlin.math.sqrt

/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/ /**
 * A class containing static math methods useful for image processing.
 */
object ImageMath {
    const val PI = Math.PI.toFloat()
    const val HALF_PI = Math.PI.toFloat() / 2.0f
    const val QUARTER_PI = Math.PI.toFloat() / 4.0f
    const val TWO_PI = Math.PI.toFloat() * 2.0f

    /**
     * Apply a bias to a number in the unit interval, moving numbers towards 0 or 1
     * according to the bias parameter.
     * @param a the number to bias
     * @param b the bias parameter. 0.5 means no change, smaller values bias towards 0, larger towards 1.
     * @return the output value
     */
    fun bias(a: Float, b: Float): Float {
//		return (float)Math.pow(a, Math.log(b) / Math.log(0.5));
        return a / ((1.0f / b - 2) * (1.0f - a) + 1)
    }

    /**
     * A variant of the gamma function.
     * @param a the number to apply gain to
     * @param b the gain parameter. 0.5 means no change, smaller values reduce gain, larger values increase gain.
     * @return the output value
     */
    fun gain(a: Float, b: Float): Float {
        /*
		float p = (float)Math.log(1.0 - b) / (float)Math.log(0.5);

		if (a < .001)
			return 0.0f;
		else if (a > .999)
			return 1.0f;
		if (a < 0.5)
			return (float)Math.pow(2 * a, p) / 2;
		else
			return 1.0f - (float)Math.pow(2 * (1. - a), p) / 2;
*/
        val c = (1.0f / b - 2.0f) * (1.0f - 2.0f * a)
        return if (a < 0.5) a / (c + 1.0f) else (c - a) / (c - 1.0f)
    }

    /**
     * The step function. Returns 0 below a threshold, 1 above.
     * @param a the threshold position
     * @param x the input parameter
     * @return the output value - 0 or 1
     */
    fun step(a: Float, x: Float): Float {
        return if (x < a) 0.0f else 1.0f
    }

    /**
     * The pulse function. Returns 1 between two thresholds, 0 outside.
     * @param a the lower threshold position
     * @param b the upper threshold position
     * @param x the input parameter
     * @return the output value - 0 or 1
     */
    fun pulse(a: Float, b: Float, x: Float): Float {
        return if (x < a || x >= b) 0.0f else 1.0f
    }

    /**
     * A smoothed pulse function. A cubic function is used to smooth the step between two thresholds.
     * @param a1 the lower threshold position for the start of the pulse
     * @param a2 the upper threshold position for the start of the pulse
     * @param b1 the lower threshold position for the end of the pulse
     * @param b2 the upper threshold position for the end of the pulse
     * @param x the input parameter
     * @return the output value
     */
    fun smoothPulse(a1: Float, a2: Float, b1: Float, b2: Float, x: Float): Float {
        var x = x
        if (x < a1 || x >= b2) return 0f
        if (x >= a2) {
            if (x < b1) return 1.0f
            x = (x - b1) / (b2 - b1)
            return 1.0f - x * x * (3.0f - 2.0f * x)
        }
        x = (x - a1) / (a2 - a1)
        return x * x * (3.0f - 2.0f * x)
    }

    /**
     * A smoothed step function. A cubic function is used to smooth the step between two thresholds.
     * @param a the lower threshold position
     * @param b the upper threshold position
     * @param x the input parameter
     * @return the output value
     */
    fun smoothStep(a: Float, b: Float, x: Float): Float {
        var x = x
        if (x < a) return 0f
        if (x >= b) return 1f
        x = (x - a) / (b - a)
        return x * x * (3f - 2f * x)
    }

    /**
     * A "circle up" function. Returns y on a unit circle given 1-x. Useful for forming bevels.
     * @param x the input parameter in the range 0..1
     * @return the output value
     */
    fun circleUp(x: Float): Float {
        var x = x
        x = 1f - x
        return sqrt((1f - x * x).toDouble()).toFloat()
    }

    /**
     * A "circle down" function. Returns 1-y on a unit circle given x. Useful for forming bevels.
     * @param x the input parameter in the range 0..1
     * @return the output value
     */
    fun circleDown(x: Float): Float {
        return 1.0f - sqrt((1f - x * x).toDouble()).toFloat()
    }

    /**
     * Clamp a value to an interval.
     * @param a the lower clamp threshold
     * @param b the upper clamp threshold
     * @param x the input parameter
     * @return the clamped value
     */
    fun clamp(x: Float, a: Float, b: Float): Float {
        return if (x < a) a else if (x > b) b else x
    }

    /**
     * Clamp a value to an interval.
     * @param a the lower clamp threshold
     * @param b the upper clamp threshold
     * @param x the input parameter
     * @return the clamped value
     */
    fun clamp(x: Int, a: Int, b: Int): Int {
        return if (x < a) a else if (x > b) b else x
    }

    /**
     * Return a mod b. This differs from the % operator with respect to negative numbers.
     * @param a the dividend
     * @param b the divisor
     * @return a mod b
     */
    @JvmStatic
    fun mod(a: Double, b: Double): Double {
        var a = a
        val n = (a / b).toInt()
        a -= n * b
        return if (a < 0) a + b else a
    }

    /**
     * Return a mod b. This differs from the % operator with respect to negative numbers.
     * @param a the dividend
     * @param b the divisor
     * @return a mod b
     */
    fun mod(a: Float, b: Float): Float {
        var a = a
        val n = (a / b).toInt()
        a -= n * b
        return if (a < 0) a + b else a
    }

    /**
     * Return a mod b. This differs from the % operator with respect to negative numbers.
     * @param a the dividend
     * @param b the divisor
     * @return a mod b
     */
    fun mod(a: Int, b: Int): Int {
        var a = a
        val n = a / b
        a -= n * b
        return if (a < 0) a + b else a
    }

    /**
     * The triangle function. Returns a repeating triangle shape in the range 0..1 with wavelength 1.0
     * @param x the input parameter
     * @return the output value
     */
    fun triangle(x: Float): Float {
        val r = mod(x, 1.0f)
        return 2.0f * if (r < 0.5f) r else 1f - r
    }

    /**
     * Linear interpolation.
     * @param t the interpolation parameter
     * @param a the lower interpolation range
     * @param b the upper interpolation range
     * @return the interpolated value
     */
    fun lerp(t: Float, a: Float, b: Float): Float {
        return a + t * (b - a)
    }

    /**
     * Linear interpolation.
     * @param t the interpolation parameter
     * @param a the lower interpolation range
     * @param b the upper interpolation range
     * @return the interpolated value
     */
    fun lerp(t: Float, a: Int, b: Int): Int {
        return (a + t * (b - a)).toInt()
    }

    /**
     * Linear interpolation of ARGB values.
     * @param t the interpolation parameter
     * @param rgb1 the lower interpolation range
     * @param rgb2 the upper interpolation range
     * @return the interpolated value
     */
    fun mixColors(t: Float, rgb1: Int, rgb2: Int): Int {
        var a1 = rgb1 shr 24 and 0xff
        var r1 = rgb1 shr 16 and 0xff
        var g1 = rgb1 shr 8 and 0xff
        var b1 = rgb1 and 0xff
        val a2 = rgb2 shr 24 and 0xff
        val r2 = rgb2 shr 16 and 0xff
        val g2 = rgb2 shr 8 and 0xff
        val b2 = rgb2 and 0xff
        a1 = lerp(t, a1, a2)
        r1 = lerp(t, r1, r2)
        g1 = lerp(t, g1, g2)
        b1 = lerp(t, b1, b2)
        return a1 shl 24 or (r1 shl 16) or (g1 shl 8) or b1
    }

    /**
     * Bilinear interpolation of ARGB values.
     * @param x the X interpolation parameter 0..1
     * @param y the y interpolation parameter 0..1
     * @param rgb array of four ARGB values in the order NW, NE, SW, SE
     * @return the interpolated value
     */
    fun bilinearInterpolate(x: Float, y: Float, p: IntArray): Int {
        var m0: Float
        var m1: Float
        val a0 = p[0] shr 24 and 0xff
        val r0 = p[0] shr 16 and 0xff
        val g0 = p[0] shr 8 and 0xff
        val b0 = p[0] and 0xff
        val a1 = p[1] shr 24 and 0xff
        val r1 = p[1] shr 16 and 0xff
        val g1 = p[1] shr 8 and 0xff
        val b1 = p[1] and 0xff
        val a2 = p[2] shr 24 and 0xff
        val r2 = p[2] shr 16 and 0xff
        val g2 = p[2] shr 8 and 0xff
        val b2 = p[2] and 0xff
        val a3 = p[3] shr 24 and 0xff
        val r3 = p[3] shr 16 and 0xff
        val g3 = p[3] shr 8 and 0xff
        val b3 = p[3] and 0xff
        val cx = 1.0f - x
        val cy = 1.0f - y
        m0 = cx * a0 + x * a1
        m1 = cx * a2 + x * a3
        val a = (cy * m0 + y * m1).toInt()
        m0 = cx * r0 + x * r1
        m1 = cx * r2 + x * r3
        val r = (cy * m0 + y * m1).toInt()
        m0 = cx * g0 + x * g1
        m1 = cx * g2 + x * g3
        val g = (cy * m0 + y * m1).toInt()
        m0 = cx * b0 + x * b1
        m1 = cx * b2 + x * b3
        val b = (cy * m0 + y * m1).toInt()
        return a shl 24 or (r shl 16) or (g shl 8) or b
    }

    /**
     * Return the NTSC gray level of an RGB value.
     * @param rgb1 the input pixel
     * @return the gray level (0-255)
     */
    fun brightnessNTSC(rgb: Int): Int {
        val r = rgb shr 16 and 0xff
        val g = rgb shr 8 and 0xff
        val b = rgb and 0xff
        return (r * 0.299f + g * 0.587f + b * 0.114f).toInt()
    }

    // Catmull-Rom splines
    private const val m00 = -0.5f
    private const val m01 = 1.5f
    private const val m02 = -1.5f
    private const val m03 = 0.5f
    private const val m10 = 1.0f
    private const val m11 = -2.5f
    private const val m12 = 2.0f
    private const val m13 = -0.5f
    private const val m20 = -0.5f
    private const val m21 = 0.0f
    private const val m22 = 0.5f
    private const val m23 = 0.0f
    private const val m30 = 0.0f
    private const val m31 = 1.0f
    private const val m32 = 0.0f
    private const val m33 = 0.0f

    /**
     * Compute a Catmull-Rom spline.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param knots the array of knots
     * @return the spline value
     */
    fun spline(x: Float, numKnots: Int, knots: FloatArray): Float {
        var x = x
        var span: Int
        val numSpans = numKnots - 3
        val k0: Float
        val k1: Float
        val k2: Float
        val k3: Float
        val c0: Float
        val c1: Float
        val c2: Float
        val c3: Float
        require(numSpans >= 1) { "Too few knots in spline" }
        x = clamp(x, 0f, 1f) * numSpans
        span = x.toInt()
        if (span > numKnots - 4) span = numKnots - 4
        x -= span.toFloat()
        k0 = knots[span]
        k1 = knots[span + 1]
        k2 = knots[span + 2]
        k3 = knots[span + 3]
        c3 = m00 * k0 + m01 * k1 + m02 * k2 + m03 * k3
        c2 = m10 * k0 + m11 * k1 + m12 * k2 + m13 * k3
        c1 = m20 * k0 + m21 * k1 + m22 * k2 + m23 * k3
        c0 = m30 * k0 + m31 * k1 + m32 * k2 + m33 * k3
        return ((c3 * x + c2) * x + c1) * x + c0
    }

    /**
     * Compute a Catmull-Rom spline, but with variable knot spacing.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param xknots the array of knot x values
     * @param yknots the array of knot y values
     * @return the spline value
     */
    fun spline(x: Float, numKnots: Int, xknots: IntArray, yknots: IntArray): Float {
        var span: Int
        val numSpans = numKnots - 3
        val k0: Float
        val k1: Float
        val k2: Float
        val k3: Float
        val c0: Float
        val c1: Float
        val c2: Float
        val c3: Float
        require(numSpans >= 1) { "Too few knots in spline" }
        span = 0
        while (span < numSpans) {
            if (xknots[span + 1] > x) break
            span++
        }
        if (span > numKnots - 3) span = numKnots - 3
        var t = (x - xknots[span]) / (xknots[span + 1] - xknots[span])
        span--
        if (span < 0) {
            span = 0
            t = 0f
        }
        k0 = yknots[span].toFloat()
        k1 = yknots[span + 1].toFloat()
        k2 = yknots[span + 2].toFloat()
        k3 = yknots[span + 3].toFloat()
        c3 = m00 * k0 + m01 * k1 + m02 * k2 + m03 * k3
        c2 = m10 * k0 + m11 * k1 + m12 * k2 + m13 * k3
        c1 = m20 * k0 + m21 * k1 + m22 * k2 + m23 * k3
        c0 = m30 * k0 + m31 * k1 + m32 * k2 + m33 * k3
        return ((c3 * t + c2) * t + c1) * t + c0
    }

    /**
     * Compute a Catmull-Rom spline for RGB values.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param knots the array of knots
     * @return the spline value
     */
    fun colorSpline(x: Float, numKnots: Int, knots: IntArray): Int {
        var x = x
        var span: Int
        val numSpans = numKnots - 3
        var k0: Float
        var k1: Float
        var k2: Float
        var k3: Float
        var c0: Float
        var c1: Float
        var c2: Float
        var c3: Float
        require(numSpans >= 1) { "Too few knots in spline" }
        x = clamp(x, 0f, 1f) * numSpans
        span = x.toInt()
        if (span > numKnots - 4) span = numKnots - 4
        x -= span.toFloat()
        var v = 0
        for (i in 0..3) {
            val shift = i * 8
            k0 = (knots[span] shr shift and 0xff).toFloat()
            k1 = (knots[span + 1] shr shift and 0xff).toFloat()
            k2 = (knots[span + 2] shr shift and 0xff).toFloat()
            k3 = (knots[span + 3] shr shift and 0xff).toFloat()
            c3 = m00 * k0 + m01 * k1 + m02 * k2 + m03 * k3
            c2 = m10 * k0 + m11 * k1 + m12 * k2 + m13 * k3
            c1 = m20 * k0 + m21 * k1 + m22 * k2 + m23 * k3
            c0 = m30 * k0 + m31 * k1 + m32 * k2 + m33 * k3
            var n = (((c3 * x + c2) * x + c1) * x + c0).toInt()
            if (n < 0) n = 0 else if (n > 255) n = 255
            v = v or (n shl shift)
        }
        return v
    }

    /**
     * Compute a Catmull-Rom spline for RGB values, but with variable knot spacing.
     * @param x the input parameter
     * @param numKnots the number of knots in the spline
     * @param xknots the array of knot x values
     * @param yknots the array of knot y values
     * @return the spline value
     */
    fun colorSpline(x: Int, numKnots: Int, xknots: IntArray, yknots: IntArray): Int {
        var span: Int
        val numSpans = numKnots - 3
        var k0: Float
        var k1: Float
        var k2: Float
        var k3: Float
        var c0: Float
        var c1: Float
        var c2: Float
        var c3: Float
        require(numSpans >= 1) { "Too few knots in spline" }
        span = 0
        while (span < numSpans) {
            if (xknots[span + 1] > x) break
            span++
        }
        if (span > numKnots - 3) span = numKnots - 3
        var t = (x - xknots[span]).toFloat() / (xknots[span + 1] - xknots[span])
        span--
        if (span < 0) {
            span = 0
            t = 0f
        }
        var v = 0
        for (i in 0..3) {
            val shift = i * 8
            k0 = (yknots[span] shr shift and 0xff).toFloat()
            k1 = (yknots[span + 1] shr shift and 0xff).toFloat()
            k2 = (yknots[span + 2] shr shift and 0xff).toFloat()
            k3 = (yknots[span + 3] shr shift and 0xff).toFloat()
            c3 = m00 * k0 + m01 * k1 + m02 * k2 + m03 * k3
            c2 = m10 * k0 + m11 * k1 + m12 * k2 + m13 * k3
            c1 = m20 * k0 + m21 * k1 + m22 * k2 + m23 * k3
            c0 = m30 * k0 + m31 * k1 + m32 * k2 + m33 * k3
            var n = (((c3 * t + c2) * t + c1) * t + c0).toInt()
            if (n < 0) n = 0 else if (n > 255) n = 255
            v = v or (n shl shift)
        }
        return v
    }

    /**
     * An implementation of Fant's resampling algorithm.
     * @param source the source pixels
     * @param dest the destination pixels
     * @param length the length of the scanline to resample
     * @param offset the start offset into the arrays
     * @param stride the offset between pixels in consecutive rows
     * @param out an array of output positions for each pixel
     */
    fun resample(source: IntArray, dest: IntArray, length: Int, offset: Int, stride: Int, out: FloatArray) {
        var i: Int
        var j: Int
        var intensity: Float
        var sizfac: Float
        var inSegment: Float
        var outSegment: Float
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var nextA: Int
        var nextR: Int
        var nextG: Int
        var nextB: Int
        var aSum: Float
        var rSum: Float
        var gSum: Float
        var bSum: Float
        val `in`: FloatArray
        var srcIndex = offset
        var destIndex = offset
        val lastIndex = source.size
        var rgb: Int
        `in` = FloatArray(length + 1)
        i = 0
        j = 0
        while (j < length) {
            while (out[i + 1] < j) i++
            `in`[j] = i + (j - out[i]) / (out[i + 1] - out[i])
            j++
        }
        `in`[length] = length.toFloat()
        inSegment = 1.0f
        outSegment = `in`[1]
        sizfac = outSegment
        bSum = 0.0f
        gSum = bSum
        rSum = gSum
        aSum = rSum
        rgb = source[srcIndex]
        a = rgb shr 24 and 0xff
        r = rgb shr 16 and 0xff
        g = rgb shr 8 and 0xff
        b = rgb and 0xff
        srcIndex += stride
        rgb = source[srcIndex]
        nextA = rgb shr 24 and 0xff
        nextR = rgb shr 16 and 0xff
        nextG = rgb shr 8 and 0xff
        nextB = rgb and 0xff
        srcIndex += stride
        i = 1
        while (i < length) {
            val aIntensity = inSegment * a + (1.0f - inSegment) * nextA
            val rIntensity = inSegment * r + (1.0f - inSegment) * nextR
            val gIntensity = inSegment * g + (1.0f - inSegment) * nextG
            val bIntensity = inSegment * b + (1.0f - inSegment) * nextB
            if (inSegment < outSegment) {
                aSum += aIntensity * inSegment
                rSum += rIntensity * inSegment
                gSum += gIntensity * inSegment
                bSum += bIntensity * inSegment
                outSegment -= inSegment
                inSegment = 1.0f
                a = nextA
                r = nextR
                g = nextG
                b = nextB
                if (srcIndex < lastIndex) rgb = source[srcIndex]
                nextA = rgb shr 24 and 0xff
                nextR = rgb shr 16 and 0xff
                nextG = rgb shr 8 and 0xff
                nextB = rgb and 0xff
                srcIndex += stride
            } else {
                aSum += aIntensity * outSegment
                rSum += rIntensity * outSegment
                gSum += gIntensity * outSegment
                bSum += bIntensity * outSegment
                dest[destIndex] = min((aSum / sizfac).toDouble(), 255.0).toInt() shl 24 or
                        (min((rSum / sizfac).toDouble(), 255.0).toInt() shl 16) or
                        (min((gSum / sizfac).toDouble(), 255.0).toInt() shl 8) or min(
                    (bSum / sizfac).toDouble(),
                    255.0
                ).toInt()
                destIndex += stride
                bSum = 0.0f
                gSum = bSum
                rSum = gSum
                inSegment -= outSegment
                outSegment = `in`[i + 1] - `in`[i]
                sizfac = outSegment
                i++
            }
        }
    }
}
