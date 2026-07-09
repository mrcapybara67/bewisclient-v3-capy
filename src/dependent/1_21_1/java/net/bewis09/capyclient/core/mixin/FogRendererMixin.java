package net.bewis09.capyclient.core.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bewis09.capyclient.features.utilities.BetterVisibility;
import net.bewis09.capyclient.features.utilities.FogData;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow
    @Nullable
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity entity, float f) {
        return null;
    }

    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void capyclient$applyFog(Camera camera, FogRenderer.FogMode fogMode, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfo ci) {
        if(!BetterVisibility.INSTANCE.getEnabled().get()) return;

        FogType cameraSubmersionType = camera.getFluidInCamera();
        FogData fogData = new FogData(RenderSystem.getShaderFogStart(), RenderSystem.getShaderFogEnd());

        if (cameraSubmersionType == FogType.LAVA) {
            BetterVisibility.INSTANCE.applyFogModifier("lava", fogData, viewDistance);
        } else if (cameraSubmersionType == FogType.POWDER_SNOW) {
            BetterVisibility.INSTANCE.applyFogModifier("powder_snow", fogData, viewDistance);
        } else if (getPriorityFogFunction(camera.getEntity(), tickDelta) != null) {
            return;
        } else if (cameraSubmersionType == FogType.WATER) {
            BetterVisibility.INSTANCE.applyFogModifier("water", fogData, viewDistance);
        } else if (thickenFog) {
            BetterVisibility.INSTANCE.applyFogModifier("atmospheric", fogData, viewDistance);
        } else {
            return;
        }

        RenderSystem.setShaderFogStart(fogData.getEnvironmentalStart());
        RenderSystem.setShaderFogEnd(fogData.getEnvironmentalEnd());
    }
}
