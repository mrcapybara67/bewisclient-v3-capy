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
    // Cache stateless fog-environment instances — they have no per-frame state and
    // were being constructed every frame in the previous setupFog() body, generating
    // measurable GC pressure. Holding a single instance per type removes that allocation.
    private static final BlindnessFogEnvironment BLINDNESS_FOG = new BlindnessFogEnvironment();
    private static final DarknessFogEnvironment DARKNESS_FOG = new DarknessFogEnvironment();

    @Shadow
    protected abstract FogType getFogType(Camera camera);

    @Redirect(method = "setupFog", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/fog/FogData;environmentalStart:F", opcode = Opcodes.GETFIELD))
    private float capyclient$applyFog(FogData fogData) {
        // Disabled-first early-out — when the feature is OFF we must not allocate anything.
        if (!BetterVisibility.INSTANCE.getEnabled().get()) return fogData.environmentalStart;

        Minecraft client = Minecraft.getInstance();
        Camera camera = client.gameRenderer.getMainCamera();
        FogType cameraSubmersionType = camera.getFluidInCamera();
        float viewDistance = (float)(client.options.getEffectiveRenderDistance() * 16);

        if (cameraSubmersionType == FogType.LAVA) {
            BetterVisibility.INSTANCE.applyFogModifier("lava", fogData, viewDistance);
            return fogData.environmentalStart;
        }
        if (cameraSubmersionType == FogType.POWDER_SNOW) {
            BetterVisibility.INSTANCE.applyFogModifier("powder_snow", fogData, viewDistance);
            return fogData.environmentalStart;
        }

        FogType currentFogType = this.getFogType(camera);
        if (BLINDNESS_FOG.isApplicable(currentFogType, camera.entity())
                || DARKNESS_FOG.isApplicable(currentFogType, camera.entity())) {
            return fogData.environmentalStart;
        }

        if (cameraSubmersionType == FogType.WATER) {
            BetterVisibility.INSTANCE.applyFogModifier("water", fogData, viewDistance);
        } else if (currentFogType == FogType.ATMOSPHERIC) {
            BetterVisibility.INSTANCE.applyFogModifier("atmospheric", fogData, viewDistance);
        }
        return fogData.environmentalStart;
    }
}
