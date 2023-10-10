package utils.image.color

import java.awt.Color
import java.awt.Color.HSBtoRGB
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.ImageIO.read


/**
 * from https://stackoverflow.com/questions/35255932/java-change-saturation-of-image
 * */
object ImageFilter {

    /**
     * 调整色相、饱和度、亮度
     *
     * @param image 图片
     * @param satuPer 饱和度
     * @param huePer 色相
     * @param lumPer 亮度
     * */
    fun hslImage(image: BufferedImage, satuPer: Float, huePer: Float, lumPer: Float): BufferedImage {
        val bimg = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until bimg.height) {
            for (x in 0 until bimg.width) {
                val pixel = image.getRGB(x, y)
                val r = pixel shr 16 and 0xFF
                val g = pixel shr 8 and 0xFF
                val b = pixel and 0xFF

                val hsb: FloatArray = Color.RGBtoHSB(r, g, b, null)
                val hue = (hsb[0] + hsb[0] * huePer).coerceIn(0f, 1f)
                val saturation = (hsb[1] + hsb[1] * satuPer).coerceIn(0f, 1f)
                val brightness = (hsb[2] + hsb[2] * lumPer).coerceIn(0f, 1f)

                val rgb = HSBtoRGB(hue, saturation, brightness)

                bimg.setRGB(x, y, rgb)
            }
        }
        return bimg
    }

    fun test() {
        val img: BufferedImage = read(File("C:\\Users\\DELL\\Desktop\\test\\DSC08468(1).jpg"))

        var img2: BufferedImage? = hslImage(img, 0.15f, 0f, 0f)
        ImageIO.write(img2, "jpeg", File("C:\\Users\\DELL\\Desktop\\test\\test-s+.jpg"))
        img2 = hslImage(img, -0.5f, 0f, 0f)
        ImageIO.write(img2, "jpeg", File("C:\\Users\\DELL\\Desktop\\test\\test-s-.jpg"))
    }
}