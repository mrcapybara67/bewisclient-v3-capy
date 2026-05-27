package net.bewis09.bewisclient.mixin.client

import net.bewis09.bewisclient.impl.functionalities.EntityHighlight
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(GameRenderer::class)
abstract class OverlayTextureChangerMixin {
    @Unique
    var alpha: Float = EntityHighlight.alpha.get()

    @Unique
    var color: Int = EntityHighlight.color.get().getColorInt()

    @Unique
    var enabled: Boolean = EntityHighlight.isEnabled()

    @Unique
    var overlayTexture: OverlayTexture = OverlayTexture()

    @Inject(method = ["overlayTexture"], at = [At("HEAD")], cancellable = true)
    fun getOverlayTexture(cir: CallbackInfoReturnable<OverlayTexture?>) {
        if (enabled != EntityHighlight.isEnabled() || enabled && (alpha != EntityHighlight.alpha.get() || color != (EntityHighlight.color.get().getColorInt()))) {
            alpha = EntityHighlight.alpha.get()
            color = EntityHighlight.color.get().getColorInt()
            enabled = EntityHighlight.isEnabled()

            overlayTexture = OverlayTexture()
        }

        cir.returnValue = overlayTexture
    }
}