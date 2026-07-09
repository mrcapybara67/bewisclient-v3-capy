// @VersionReplacement

package net.bewis09.bewisclient.mixin.client;

import net.bewis09.bewisclient.features.utilities.FireHeight;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {
    // @[26.1] "renderFire" @[] "lambda$submitFire$0"
    @ModifyArg(method = /*[@]*/"renderFire"/*[!@]*/, at = @At(
            value = "INVOKE",
            // @[26.1] "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V" @[] "Lorg/joml/Matrix4f;translate(FFF)Lorg/joml/Matrix4f;"
            target = /*[@]*/"Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"/*[!@]*/
    ), index = 1)
    private static float modifyTranslateY(float y) {
        if (!FireHeight.INSTANCE.isEnabled()) return y;
        // static = leave the overlay at its vanilla vertical position (height setting ignored).
        String mode = FireHeight.INSTANCE.getRenderMode().get();
        if ("static".equalsIgnoreCase(mode)) return y;
        float base = y - (1 - FireHeight.INSTANCE.getHeight().get()) * 0.4f;
        // verticalOffset is a -1..1 slider; treat each unit as 0.4 of a screen unit.
        return base + FireHeight.INSTANCE.getVerticalOffset().get() * 0.4f;
    }

    /*
     * Scale the overlay's tint alpha by FireHeight.opacity (0 = invisible, 1 = vanilla).
     * In 1.21.11, renderFire does NOT call RenderSystem.setShaderColor — it now
     * calls VertexConsumer.setColor(float, float, float, float) directly on each
     * vertex (with alpha = 0.9f).  We intercept those setColor calls instead.
     * The `require = 0` prevents a game crash if Mojang refactors this again.
     */
    @ModifyArg(method = "renderFire", at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)V"
    ), index = 3, require = 0)
    private static float modifyFireAlpha(float alpha) {
        if (!FireHeight.INSTANCE.isEnabled()) return alpha;
        return alpha * FireHeight.INSTANCE.getOpacity().get();
    }
}