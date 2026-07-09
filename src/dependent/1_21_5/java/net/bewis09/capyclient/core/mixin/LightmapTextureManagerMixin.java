package net.bewis09.capyclient.core.mixin;

import net.bewis09.capyclient.features.utilities.Fullbright;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
abstract class LightmapTextureManagerMixin {
    @Redirect(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float invokeGamma(Double instance) {
        if (!Fullbright.INSTANCE.isEnabled()) {
            return instance.floatValue();
        }
        return Fullbright.INSTANCE.getBrightness().get();
    }
}