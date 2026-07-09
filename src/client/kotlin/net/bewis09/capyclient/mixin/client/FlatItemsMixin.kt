// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import com.mojang.blaze3d.vertex.PoseStack
import net.bewis09.capyclient.features.utilities.FlatItems
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Replaces the 3D model rendering of dropped [ItemEntity] instances with
 * a flat 2D sprite render.
 *
 * Targets `ItemRenderer.renderStatic` (1.21.11+) or `ItemRenderer.render`
 * (older versions) and intercepts calls where the `livingEntity` parameter
 * is an `ItemEntity`.
 */
@Mixin(ItemRenderer::class)
class FlatItemsMixin {

    @Unique
    private fun isFlatItemsEnabled(): Boolean = FlatItems.isEnabled()

    /**
     * In 1.21.11 the method is `renderStatic` without a `leftHand` param:
     *   renderStatic(ItemStack, ItemDisplayContext, PoseStack, MultiBufferSource, int, int, LivingEntity)
     * In older versions the method is:
     *   render(ItemStack, ItemDisplayContext, boolean, PoseStack, MultiBufferSource, int, int, LivingEntity)
     *
     * We inject into both overloads and delegate to the same flat-render logic.
     */

    // @[1.21.11] "renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/entity/LivingEntity;)V" @[] void noop() — 1.21.11-only overload
    @Inject(
        method = [/*[@]*/"renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/entity/LivingEntity;)V"/*[!@]*/],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun onRenderStatic(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int,
        livingEntity: LivingEntity?,
        ci: CallbackInfo
    ) {
        handleFlatItemRender(stack, poseStack, buffer, combinedLight, combinedOverlay, livingEntity, ci)
    }

    // @[] "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/entity/LivingEntity;)V" @[1.21.11] void noop() — older-versions-only overload
    @Inject(
        method = [/*[@]*/"render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/world/entity/LivingEntity;)V"/*[!@]*/],
        at = [At("HEAD")],
        cancellable = true
    )
    private fun onRender(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        leftHand: Boolean,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int,
        livingEntity: LivingEntity?,
        ci: CallbackInfo
    ) {
        handleFlatItemRender(stack, poseStack, buffer, combinedLight, combinedOverlay, livingEntity, ci)
    }

    @Unique
    private fun handleFlatItemRender(
        stack: ItemStack,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int,
        livingEntity: LivingEntity?,
        ci: CallbackInfo
    ) {
        if (!isFlatItemsEnabled()) return
        // Only intercept when rendering a dropped ItemEntity, not GUI/hand/inventory.
        if (livingEntity !is ItemEntity) return

        ci.cancel()

        val mc = Minecraft.getInstance()
        val itemRenderer = mc.itemRenderer
        val model: BakedModel = itemRenderer.getModel(stack, mc.level, livingEntity, 0)

        poseStack.pushPose()

        val size = FlatItems.spriteSize.get().toFloat() / 16f
        poseStack.scale(size, size, size)

        // Billboard: rotate the sprite to always face the camera.
        // Uses `camera.rotation()` which is available in both Yarn and
        // Mojang mappings across all 1.21+ versions.
        if (FlatItems.billboard.get()) {
            poseStack.mulPose(mc.gameRenderer.mainCamera.rotation())
        }

        // Render as a flat GUI sprite.
        itemRenderer.render(
            stack,
            ItemDisplayContext.GUI,
            false,
            poseStack,
            buffer,
            combinedLight,
            combinedOverlay,
            model
        )

        poseStack.popPose()
    }
}
