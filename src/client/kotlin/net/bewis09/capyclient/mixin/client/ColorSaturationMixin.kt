// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import com.mojang.blaze3d.platform.NativeImage
import net.bewis09.capyclient.features.utilities.ColorSaturation
import net.minecraft.client.renderer.LightTexture
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [LightTexture.updateLightTexture] to post-process the
 * light-map pixel data and adjust colour saturation.
 *
 * After the vanilla light-map texture is computed we iterate every pixel
 * and apply a per-pixel saturation transformation:
 *
 *     gray  = 0.299×R + 0.587×G + 0.114×B
 *     R'    = clamp(gray + sat × (R − gray))
 *     G'    = clamp(gray + sat × (G − gray))
 *     B'    = clamp(gray + sat × (B − gray))
 *
 * sat = 0.0 → grayscale, sat = 1.0 → vanilla, sat = 2.0 → double saturation.
 */
@Mixin(LightTexture::class)
abstract class ColorSaturationMixin {

    @Shadow
    // @[1.21.1] public com.mojang.blaze3d.platform.NativeImage image; @[] public com.mojang.blaze3d.platform.NativeImage lightTexture;
    // Map the LightTexture's internal NativeImage — Mojang renamed the field
    // several times across versions.  The @Shadow picks the correct field
    // at compile time.
    private NativeImage /*[@]*/lightTexture/*[!@]*/ = null!!

    @Unique
    private var lastAppliedSaturation: Float = 1.0f

    @Inject(method = ["updateLightTexture"], at = [At("RETURN")])
    private fun onPostUpdateLightTexture(tickDelta: Float, ci: CallbackInfo) {
        val img = this/*[@]*/.lightTexture/*[!@]*/
        val w = img.width
        val h = img.height

        if (!ColorSaturation.isEnabled()) {
            // BUGFIX: When toggled OFF, restore original light texture.
            // If lastAppliedSaturation != 1.0f, the GPU still has the
            // saturated version — re-process with sat=1.0 to reset.
            if (kotlin.math.abs(lastAppliedSaturation - 1.0f) > 0.001f) {
                lastAppliedSaturation = 1.0f
                applySaturation(img, w, h, 1.0f)
            }
            return
        }

        val sat = ColorSaturation.saturationStrength.get().coerceIn(1.0f, 2.0f)

        // PERFORMANCE: cache the previous value so we skip processing when
        // nothing changed.  Without caching the per-frame pixel loop + GPU
        // texture upload would be wasted work on every frame.
        if (kotlin.math.abs(sat - lastAppliedSaturation) < 0.001f) return
        lastAppliedSaturation = sat

        if (sat == 1.0f) return

        applySaturation(img, w, h, sat)
    }

    @Unique
    private fun applySaturation(img: NativeImage, w: Int, h: Int, s: Float) {
        for (y in 0 until h) {
            for (x in 0 until w) {
                val argb = img.getPixelRGBA(x, y)
                val a = (argb shr 24 and 0xFF).toFloat()
                val r = (argb shr 16 and 0xFF).toFloat()
                val g = (argb shr 8 and 0xFF).toFloat()
                val b = (argb and 0xFF).toFloat()

                // Saturation formula
                val gray = 0.299f * r + 0.587f * g + 0.114f * b
                var nr = gray + s * (r - gray)
                var ng = gray + s * (g - gray)
                var nb = gray + s * (b - gray)

                // Clamp to valid byte range
                nr = nr.coerceIn(0f, 255f)
                ng = ng.coerceIn(0f, 255f)
                nb = nb.coerceIn(0f, 255f)

                val newPixel = (a.toInt() shl 24) or (nr.toInt() shl 16) or (ng.toInt() shl 8) or nb.toInt()
                img.setPixelRGBA(x, y, newPixel)
            }
        }

        // Upload the modified texture to GPU.
        // @[1.21.1] img.upload(0, 0, 0, 0, false) @[] img.upload(0, 0, 0, 0, false)
        img./*[@]*/upload(0, 0, 0, 0, false)/*[!@]*/
    }
}
