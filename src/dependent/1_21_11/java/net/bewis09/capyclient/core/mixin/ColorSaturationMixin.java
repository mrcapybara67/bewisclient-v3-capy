package net.bewis09.capyclient.core.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.bewis09.capyclient.features.utilities.ColorSaturation;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixes into {@link LightTexture} to post-process the light-map pixels
 * and boost colour saturation when the Color Saturation feature is on.
 *
 * After the vanilla light-map is computed, each pixel is converted
 * from RGB to HSL, the S component is multiplied by the configured
 * saturation strength (default 1.4×), and the result is written back.
 *
 * Performance: a {@code dirty} flag tracks whether any pixel actually
 * changed since the last {@link DynamicTexture#upload()}, so we never
 * upload the light map to the GPU unless saturation was actually applied.
 */
@Mixin(LightTexture.class)
public abstract class ColorSaturationMixin {

    @Accessor("lightTexture")
    public abstract DynamicTexture getLightTexture();

    /**
     * Tracks whether any pixel was modified since the last upload.
     * When {@code false} we skip the GPU upload entirely.
     */
    @Unique
    private boolean capyclientDirty = false;

    @Unique
    private float capyclientLastStrength = 1.0f;

    @Inject(method = "updateLightTexture", at = @At("RETURN"))
    private void capyclient$applySaturation(float partialTicks, CallbackInfo ci) {
        if (!ColorSaturation.INSTANCE.isEnabled()) {
            // Feature was toggled OFF — don't touch pixels, but we don't
            // need to undo anything either (next vanilla update overwrites).
            capyclientDirty = false;
            capyclientLastStrength = 1.0f;
            return;
        }

        float strength = ColorSaturation.INSTANCE.getSaturationStrength().get();
        if (strength <= 1.0f) {
            capyclientDirty = false;
            capyclientLastStrength = 1.0f;
            return; // vanilla — nothing to do
        }

        // If strength hasn't changed and we already uploaded, skip pixel loop
        if (!capyclientDirty && Math.abs(strength - capyclientLastStrength) < 0.001f) {
            return;
        }
        capyclientLastStrength = strength;

        NativeImage image = this.getLightTexture().getPixels();
        if (image == null) return;

        int w = image.getWidth();
        int h = image.getHeight();
        boolean anyChanged = false;

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

                // Compute hue once for all three channels
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
                    anyChanged = true;
                }
            }
        }

        if (anyChanged) {
            this.getLightTexture().upload();
            capyclientDirty = true;
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
