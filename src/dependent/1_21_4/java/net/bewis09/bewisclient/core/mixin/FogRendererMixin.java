package net.bewis09.bewisclient.core.mixin;

import com.mojang.blaze3d.shaders.FogShape;
import net.bewis09.bewisclient.features.utilities.BetterVisibility;
import net.bewis09.bewisclient.features.utilities.FogData;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow
    @Nullable
    private static FogRenderer.MobEffectFogFunction getPriorityFogFunction(Entity entity, float f) {
        return null;
    }

    @Inject(method = "setupFog", at = @At("RETURN"), cancellable = true)
    private static void bewisclient$applyFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfoReturnable<FogParameters> cir) {
        if(!BetterVisibility.INSTANCE.getEnabled().get()) return;

        FogParameters original = cir.getReturnValue();
        if (original == null || original == FogParameters.NO_FOG) return;

        FogType cameraSubmersionType = camera.getFluidInCamera();
        FogData fogData = new FogData(original.start(), original.end());

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

        cir.setReturnValue(new FogParameters(fogData.getEnvironmentalStart(), fogData.getEnvironmentalEnd(), FogShape.SPHERE, color.x, color.y, color.z, color.w));
    }
}
