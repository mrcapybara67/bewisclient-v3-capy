package net.bewis09.bewisclient.core.mixin;

import net.bewis09.bewisclient.features.utilities.Fullbright;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapRenderStateExtractor.class)
abstract class LightmapTextureManagerMixin {
//    @Redirect(method = "extract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/DimensionType;ambientLight()F"))
//    private float redirectAmbientLight(DimensionType instance) {
//        if (!FullbrightSettings.INSTANCE.getNightVision().get() || !FullbrightSettings.INSTANCE.isEnabled()) {
//            return instance.ambientLight();
//        }
//        return 1.0f;
//    }

    @Redirect(method = "extract", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 0))
    private float invokeGamma(Double instance) {
        if (!Fullbright.INSTANCE.isEnabled()) {
            return instance.floatValue();
        }
        return Fullbright.INSTANCE.getBrightness().get();
    }
}