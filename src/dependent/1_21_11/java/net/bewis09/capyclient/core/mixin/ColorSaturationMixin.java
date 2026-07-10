package net.bewis09.capyclient.core.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.bewis09.capyclient.features.utilities.ColorSaturation;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

/**
 * Mixes into {@link LightTexture#updateLightTexture} to boost colour
 * saturation after the vanilla light-map is computed.
 *
 * Unlike a {@code @Redirect} on {@code DynamicTexture.upload()} (which
 * fails in 1.21.11 + VulkanMod because the render-pipeline may bypass
 * the direct {@code upload()} call inside {@code updateLightTexture}),
 * this mixin uses a plain {@code @Inject} at RETURN and accesses the
 * {@code DynamicTexture} field via cached reflection.  This sidesteps
 * ALL remap / target-resolution issues.
 *
 * Each pixel is converted from RGB to HSL, the S component is multiplied
 * by the configured saturation strength (default 1.4×), and written back.
 * The texture is then re-uploaded so the GPU sees the saturated data.
 */
@Mixin(LightTexture.class)
public abstract class ColorSaturationMixin {

    /**
     * Cached reflective handle for the {@code DynamicTexture} field inside
     * {@code LightTexture}.  Resolved once on first access.
     */
    @Unique
    private static Field capyclient$textureField = null;

    @Unique
    private static boolean capyclient$fieldResolved = false;

    @Unique
    private static DynamicTexture capyclient$resolveTexture(LightTexture self) {
        if (!capyclient$fieldResolved) {
            for (Field f : LightTexture.class.getDeclaredFields()) {
                if (DynamicTexture.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    capyclient$textureField = f;
                    break;
                }
            }
            capyclient$fieldResolved = true;
        }
        if (capyclient$textureField == null) return null;
        try {
            return (DynamicTexture) capyclient$textureField.get(self);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    @Inject(method = "updateLightTexture", at = @At("RETURN"))
    private void capyclient$applySaturation(float partialTicks, CallbackInfo ci) {
        if (!ColorSaturation.INSTANCE.isEnabled()) return;

        float strength = ColorSaturation.INSTANCE.getSaturationStrength().get();
        if (strength <= 1.0f) return;

        DynamicTexture texture = capyclient$resolveTexture((LightTexture) (Object) this);
        if (texture == null) return;

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

                if (delta < 0.001f) continue;

                float l = (max + min) / 2.0f;
                float s = delta / (1.0f - Math.abs(2.0f * l - 1.0f));

                s = Math.min(1.0f, s * strength);

                float hue;
                if (max == rf) {
                    hue = ((gf - bf) / delta) / 6.0f;
                } else if (max == gf) {
                    hue = ((bf - rf) / delta + 2.0f) / 6.0f;
                } else {
                    hue = ((rf - gf) / delta + 4.0f) / 6.0f;
                }

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
