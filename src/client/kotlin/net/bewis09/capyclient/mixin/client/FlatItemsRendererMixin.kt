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
 * of dropped items when FlatItems is enabled.
 *
 * In 1.21.11 Mojang mappings, [ItemEntity] does NOT have a
 * `getBob(float)` method — the bob is entirely renderer-side.
 * [ItemEntityRenderer.extractRenderState] copies `entity.bobOffs`
 * into [ItemEntityRenderState.bobOffset], which is then used by
 * the render pipeline to create the sin-based hovering animation.
 *
 * We inject at RETURN of the specific ItemEntity overload and
 * override `state.bobOffset = 0` to cancel the bob visual.
 */
@Mixin(ItemEntityRenderer::class)
abstract class FlatItemsRendererMixin {

    @Inject(
        method = ["extractRenderState(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/client/renderer/entity/state/ItemEntityRenderState;F)V"],
        at = [At("RETURN")]
    )
    private fun onPostExtractRenderState(entity: ItemEntity, state: ItemEntityRenderState, partialTick: Float, ci: CallbackInfo) {
        if (FlatItems.isEnabled()) {
            state.bobOffset = 0f
        }
    }
}
