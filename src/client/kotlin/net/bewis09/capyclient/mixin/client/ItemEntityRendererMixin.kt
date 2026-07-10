package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.FlatItems
import net.minecraft.client.renderer.entity.ItemEntityRenderer
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [ItemEntityRenderer] to neutralise the visual bob/hover
 * effect when FlatItems (2D Items) is enabled.
 *
 * Vanilla Minecraft applies a bob animation to dropped items which
 * makes them appear to hover above the ground.  The bob is computed
 * in `extractRenderState` and stored in `state.bob`.  By zeroing
 * `state.bob` after extraction, items render at their actual world
 * position — on the ground — instead of floating in the air.
 *
 * NOTE: This mixin targets the render-state pipeline introduced in
 * 1.21.2 (Mojang mappings).  For pre-1.21.2 versions, the bob is
 * computed inside the `render` method, and this mixin will not be
 * applied.  A separate version-specific mixin would be needed for
 * those versions.
 */
@Mixin(ItemEntityRenderer::class)
abstract class ItemEntityRendererMixin {

    /**
     * Injects at RETURN of [ItemEntityRenderer.extractRenderState]
     * to zero out the bob offset, preventing items from visually
     * hovering above the ground.
     */
    @Inject(method = ["extractRenderState"], at = [At("RETURN")])
    private fun onPostExtractRenderState(entity: ItemEntity, state: ItemEntityRenderState, partialTick: Float, ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return
        // Zero out the bob so items render at ground level instead of hovering
        state.bob = 0f
    }
}
