package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.client.renderer.entity.ItemEntityRenderer
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [ItemEntityRenderer] to cancel the vanilla bob/hover
 * animation for dropped items when Item Physics is enabled.
 *
 * In 1.21.11 the bob is entirely renderer-side via
 * [ItemEntityRenderState.bobOffset]; zeroing it makes items rest on
 * the ground instead of hovering in place.
 */
@Mixin(ItemEntityRenderer::class)
abstract class ItemPhysicsRendererMixin {

    @Inject(
        method = ["extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V"],
        at = [At("RETURN")]
    )
    private fun onPostExtractRenderState(entity: ItemEntity, state: ItemEntityRenderState, partialTick: Float, ci: CallbackInfo) {
        if (ItemPhysics.isEnabled()) {
            state.bobOffset = 0f
        }
    }
}
