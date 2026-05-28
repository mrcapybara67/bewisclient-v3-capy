package net.bewis09.bewisclient.core.mixin;

import net.bewis09.bewisclient.features.utilities.Fullbright;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
abstract class LightmapTextureManagerMixin {
    @Redirect(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;ambientLight()F"))
    private float redirectAmbientLight(DimensionType instance) {
        if (!Fullbright.INSTANCE.getNightVision().get() || !Fullbright.INSTANCE.isEnabled()) {
            return instance.ambientLight();
        }
        return 1.0f;
    }

    @Redirect(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float invokeGamma(Double instance) {
        if (!Fullbright.INSTANCE.isEnabled()) {
            return instance.floatValue();
        }
        return Fullbright.INSTANCE.getBrightness().get();
    }
}