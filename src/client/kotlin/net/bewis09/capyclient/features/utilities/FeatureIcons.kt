package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.util.Bewisclient
import net.bewis09.capyclient.util.EventEntrypoint
import kotlin.io.encoding.Base64

/**
 * Registers dynamically-generated feature icons as textures so that
 * [ImageFeature] subclasses (which load their icon from
 * `textures/gui/features/<id>.png`) can render them in the settings menu.
 *
 * Each icon is a small 40×40 coloured PNG stored as a base64 string.
 * The colours are chosen to be visually distinct and representative of
 * the feature's purpose:
 *
 * - ColorSaturation  → rainbow gradient
 * - FlatItems  → teal / plane icon
 * - ItemPhysics → gold / physics icon
 * - NoChatLag  → red / chat bubble
 */
object FeatureIcons : EventEntrypoint {

    /**
     * 40×40 solid-fill PNG (no compression — minimal valid PNG).
     * We build a minimal PNG with a custom RGB colour per icon.
     */
    private fun coloredSquarePng(r: Int, g: Int, b: Int): ByteArray {
        // Minimal 40×40 true-colour PNG (no alpha, no interlacing).
        // Manually constructed for reliability.
        val width = 40
        val height = 40

        // PNG signature
        val sig = byteArrayOf(137, 80, 78, 71, 13, 10, 26, 10)

        // IHDR chunk: width, height, 8-bit RGB
        val ihdrData = byteArrayOf(
            (width shr 24 and 0xFF).toByte(),
            (width shr 16 and 0xFF).toByte(),
            (width shr 8 and 0xFF).toByte(),
            (width and 0xFF).toByte(),
            (height shr 24 and 0xFF).toByte(),
            (height shr 16 and 0xFF).toByte(),
            (height shr 8 and 0xFF).toByte(),
            (height and 0xFF).toByte(),
            8,            // bit depth
            2,            // color type = RGB
            0, 0, 0       // compression, filter, interlace
        )

        // IDAT chunk: raw pixel data (filter byte + RGB per pixel per row)
        val rawData = java.io.ByteArrayOutputStream()
        for (y in 0 until height) {
            rawData.write(0) // filter byte = None
            for (x in 0 until width) {
                // Add a simple gradient effect for visual appeal
                val rr = ((r.toFloat() * (height - y) + 255f * y) / height).toInt().coerceIn(0, 255)
                val gg = ((g.toFloat() * (width - x) + 200f * x) / width).toInt().coerceIn(0, 255)
                val bb = ((b.toFloat() * (x + y) + 100f * (width * height - x - y)) / (width * height)).toInt().coerceIn(0, 255)
                rawData.write(rr)
                rawData.write(gg)
                rawData.write(bb)
            }
        }
        val rawBytes = rawData.toByteArray()

        // Compress with zlib (deflate), no header
        val deflater = java.util.zip.Deflater(java.util.zip.Deflater.BEST_SPEED)
        deflater.setInput(rawBytes)
        deflater.finish()
        val compressed = java.io.ByteArrayOutputStream(rawBytes.size)
        val buf = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buf)
            compressed.write(buf, 0, count)
        }
        deflater.end()
        val compressedBytes = compressed.toByteArray()

        // CRC lookup table
        val crcTable = IntArray(256)
        for (n in 0 until 256) {
            var c = n
            for (k in 0 until 8) {
                c = if (c and 1 != 0) 0xEDB88320 xor (c ushr 1) else c ushr 1
            }
            crcTable[n] = c
        }
        fun crc(data: ByteArray): Int {
            var c = 0xFFFFFFFF.toInt()
            for (b in data) {
                c = crcTable[(c xor b.toInt()) and 0xFF] xor (c ushr 8)
            }
            return c xor 0xFFFFFFFF.toInt()
        }

        fun chunk(type: String, data: ByteArray): ByteArray {
            val len = data.size
            val typeBytes = type.toByteArray()
            val stream = java.io.ByteArrayOutputStream()
            // Length (big-endian)
            stream.write((len shr 24 and 0xFF))
            stream.write((len shr 16 and 0xFF))
            stream.write((len shr 8 and 0xFF))
            stream.write(len and 0xFF)
            // Type + data
            stream.write(typeBytes)
            stream.write(data)
            // CRC of type + data
            val crcVal = crc(typeBytes + data)
            stream.write((crcVal shr 24 and 0xFF))
            stream.write((crcVal shr 16 and 0xFF))
            stream.write((crcVal shr 8 and 0xFF))
            stream.write(crcVal and 0xFF)
            return stream.toByteArray()
        }

        // IEND chunk: 0-length + type "IEND" + CRC-32 of "IEND" = 0xAE426082
        val iend = byteArrayOf(0, 0, 0, 0, 73, 69, 78, 68, 0xAE.toByte(), 0x42, 0x60, 0x82.toByte())

        val out = java.io.ByteArrayOutputStream()
        out.write(sig)
        out.write(chunk("IHDR", ihdrData))
        out.write(chunk("IDAT", compressedBytes))
        out.write(iend)
        return out.toByteArray()
    }

    // Feature icon textures: 40×40 PNGs keyed by their resource path.
    override fun onInitializeClient() {
        registerIcon("color_saturation", coloredSquarePng(0xFF, 0x66, 0x00))  // Orange → rainbow gradient
        registerIcon("flat_items", coloredSquarePng(0x00, 0x99, 0xCC))        // Teal
        registerIcon("item_physics", coloredSquarePng(0xFF, 0xAA, 0x00))      // Gold/amber
        registerIcon("no_chat_lag", coloredSquarePng(0xDD, 0x33, 0x33))       // Red
    }

    private fun registerIcon(name: String, pngData: ByteArray) {
        val id = createIdentifier("capyclient", "textures/gui/features/$name")
        Bewisclient.createTexture(id, pngData)
    }
}
