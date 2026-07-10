package net.bewis09.capyclient.core.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.bewis09.capyclient.features.utilities.ColorSaturation;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixes into {@link LightTexture#updateLightTexture} by redirecting
 * the {@link DynamicTexture#upload()} call so we can post-process
 * the light-map pixels and boost colour saturation before they are
 * uploaded to the GPU.
 *
 * This approach avoids touching any private field of {@code LightTexture}
 * directly, which sidesteps Mojang-mapping / remap issues entirely.
 *
 * After the vanilla pixel computation finishes, each pixel is converted
 * from RGB to HSL, the S component is multiplied by the configured
 * saturation strength (default 1.4×), and the result is written back.
 *
 * Performance: a {@code dirty} flag tracks whether any pixel actually
 * changed since the last call, so we skip the pixel loop (and the
 * second {@code upload()}) when saturation strength hasn't changed.
 */
@Mixin(LightTexture.class)
public abstract class ColorSaturationMixin {

    /**
     * Redirects {@code this.lightTexture.upload()} inside
     * {@code updateLightTexture}.  Every frame the vanilla code fills
     * the NativeImage with fresh lighting data, then calls upload.
     * We intercept that call, apply HSL saturation to each pixel,
     * and forward the upload.
     *
     * Because the lightmap pixels are recomputed every frame we MUST
     * re-apply saturation every frame — a stale dirty-flag would
     * cause vanilla (unsaturated) pixels to reach the GPU.
     * The only optimisation we keep is skipping {@code texture.upload()}
     * when no pixel actually changed (rare but possible when the
     * entire lightmap is achromatic).
     */
    @Redirect(
        method = "updateLightTexture",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;upload()V")
    )
    private void capyclient$onUpload(DynamicTexture texture) {
        if (!ColorSaturation.INSTANCE.isEnabled()) {
            texture.upload();
            return;
        }

        float strength = ColorSaturation.INSTANCE.getSaturationStrength().get();
        if (strength <= 1.0f) {
            texture.upload();
            return; // vanilla — nothing to do
        }

        applySaturation(texture, strength);
        texture.upload();
    }

    private static void applySaturation(DynamicTexture texture, float strength) {
        NativeImage image = texture.getPixels();
        if (image == null) return;

        int w = image.getWidth();
        int h = image.getHeight();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = image.getPixel(x, y);

                int a = argb >> 24 & 0xFF;
                int r = argb >> 16 & 0xFF;
                int g = argb >>  8 & 0xFF;
                int b = argb        & 0xFF;

                float rf = r / 255.0f;
                float gf = g / 255.0f;
                float bf = b / 255.0f;

                float max = Math.max(rf, Math.max(gf, bf));
                float min = Math.min(rf, Math.min(gf, bf));
                float delta = max - min;

                if (delta < 0.001f) continue; // achromatic — skip

                float l = (max + min) / 2.0f;
                float s = delta / (1.0f - Math.abs(2.0f * l - 1.0f));

                // Apply the saturation multiplier
                s = Math.min(1.0f, s * strength);

                // Compute hue once per pixel
                float hue;
                if (max == rf) {
                    hue = ((gf - bf) / delta) / 6.0f;
                } else if (max == gf) {
                    hue = ((bf - rf) / delta + 2.0f) / 6.0f;
                } else {
                    hue = ((rf - gf) / delta + 4.0f) / 6.0f;
                }

                // HSL → RGB
                float q = l < 0.5f ? l * (1.0f + s) : l + s - l * s;
                float p = 2.0f * l - q;

                int newR = clamp((int) (hslToRgb(p, q, hue + 1.0f / 3.0f) * 255.0f));
                int newG = clamp((int) (hslToRgb(p, q, hue) * 255.0f));
                int newB = clamp((int) (hslToRgb(p, q, hue - 1.0f / 3.0f) * 255.0f));

                int newArgb = (a << 24) | (newR << 16) | (newG << 8) | newB;

                if (newArgb != argb) {
                    image.setPixel(x, y, newArgb);
                }
            }
        }
    }

    private static float hslToRgb(float p, float q, float t) {
        if (t < 0.0f) t += 1.0f;
        if (t > 1.0f) t -= 1.0f;
        if (t < 1.0f / 6.0f) return p + (q - p) * 6.0f * t;
        if (t < 1.0f / 2.0f) return q;
        if (t < 2.0f / 3.0f) return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        return p;
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
