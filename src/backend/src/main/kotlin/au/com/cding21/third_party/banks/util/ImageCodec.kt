package au.com.cding21.third_party.banks.util

import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ImageCodec(initialImage: BufferedImage) {
    var image: BufferedImage = initialImage

    companion object {
        /**
         * Factory method that parses a base64 image
         */
        @OptIn(ExperimentalEncodingApi::class)
        fun fromBase64(base64: String): ImageCodec {
            return ImageCodec(ImageIO.read(Base64.decode(base64).inputStream()))
        }
    }

    fun crop(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ): ImageCodec {
        image = image.getSubimage(x, y, width, height)
        return this
    }

    fun compare(otherImage: ImageCodec): Double {
        val totalPixels = image.width * image.height
        var matchedPixels = 0

        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                matchedPixels += if (image.getRGB(x, y) == otherImage.image.getRGB(x, y)) 1 else 0
            }
        }

        return matchedPixels.toDouble() / totalPixels
    }
}
