package net.bewis09.capyclient.core.mixin;

import net.bewis09.capyclient.features.utilities.BetterVisibility;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.BlindnessFogEnvironment;
import net.minecraft.client.renderer.fog.environment.DarknessFogEnvironment;
import net.minecraft.world.level.material.FogType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Shadow
    protected abstract FogType getFogType(Camera camera, boolean bl);

    @Redirect(method = "setupFog", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/fog/FogData;environmentalStart:F", opcode = Opcodes.GETFIELD))
    private float capyclient$applyFog(FogData fogData) {
        if(!BetterVisibility.INSTANCE.getEnabled().get()) return fogData.environmentalStart;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        FogType cameraSubmersionType = camera.getFluidInCamera();

        boolean bl = Minecraft.getInstance().level.effects().isFoggyAt(camera.getBlockPosition().getX(), camera.getBlockPosition().getZ())
                || Minecraft.getInstance().gui.getBossOverlay().shouldCreateWorldFog();

        float viewDistance = (float)(Minecraft.getInstance().options.getEffectiveRenderDistance() * 16);

        if (cameraSubmersionType == FogType.LAVA) {
            BetterVisibility.INSTANCE.applyFogModifier("lava", fogData, viewDistance);
            return fogData.environmentalStart;
        } else if (cameraSubmersionType == FogType.POWDER_SNOW) {
            BetterVisibility.INSTANCE.applyFogModifier("powder_snow", fogData, viewDistance);
            return fogData.environmentalStart;
        }

        if (new BlindnessFogEnvironment().isApplicable(this.getFogType(camera, bl), camera.getEntity()) || new DarknessFogEnvironment().isApplicable(this.getFogType(camera, bl), camera.getEntity())) {
            return fogData.environmentalStart;
        }

        if (cameraSubmersionType == FogType.WATER) {
            BetterVisibility.INSTANCE.applyFogModifier("water", fogData, viewDistance);
        } else if (getFogType(camera, bl) == FogType.DIMENSION_OR_BOSS) {
            BetterVisibility.INSTANCE.applyFogModifier("atmospheric", fogData, viewDistance);
        }
        return fogData.environmentalStart;
    }
}
