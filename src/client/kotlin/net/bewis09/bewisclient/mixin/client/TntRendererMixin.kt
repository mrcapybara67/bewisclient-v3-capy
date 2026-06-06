// @VersionReplacement

package net.bewis09.bewisclient.mixin.client

import net.bewis09.bewisclient.features.utilities.TntTimer
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.TntRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.item.PrimedTnt
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("unused")
@Mixin(TntRenderer::class)
// @[1.21.1] PrimedTnt @[] PrimedTnt, net.minecraft.client.renderer.entity.state.TntRenderState
abstract class TntRendererMixin(context: EntityRendererProvider.Context): EntityRenderer</*[@]*/PrimedTnt, net.minecraft.client.renderer.entity.state.TntRenderState/*[!@]*/>(context) {
    @Inject(
        // @[1.21.1] "render" @[] "extractRenderState(Lnet/minecraft/world/entity/item/PrimedTnt;Lnet/minecraft/client/renderer/entity/state/TntRenderState;F)V"
        method = [/*[@]*/"extractRenderState(Lnet/minecraft/world/entity/item/PrimedTnt;Lnet/minecraft/client/renderer/entity/state/TntRenderState;F)V"/*[!@]*/],
        at = [At("RETURN")],
    )
    // @[1.21.1] primedTnt: PrimedTnt, f: Float, g: Float, poseStack: com.mojang.blaze3d.vertex.PoseStack, multiBufferSource: net.minecraft.client.renderer.MultiBufferSource, i: Int, ci: CallbackInfo @[] primedTnt: PrimedTnt, state: net.minecraft.client.renderer.entity.state.TntRenderState, f: Float, ci: CallbackInfo
    fun onSubmit(/*[@]*/primedTnt: PrimedTnt, state: net.minecraft.client.renderer.entity.state.TntRenderState, f: Float, ci: CallbackInfo/*[!@]*/) {
        if (TntTimer.isEnabled() && this.entityRenderDispatcher.distanceToSqr(primedTnt) <= 144.0) {
            // @[1.21.1] val nameTag @[] state.nameTag
            /*[@]*/state.nameTag/*[!@]*/ = Component.literal(((primedTnt.fuse / 2f).toInt() / 10f).toString().let { if (it.contains(".")) it else "$it.0" } + "s")
            // @[1.21.1] this.renderNameTag(primedTnt, nameTag, poseStack, multiBufferSource, i, g) @[] state.nameTagAttachment = net.minecraft.world.phys.Vec3(0.0, 1.0, 0.0)
            /*[@]*/state.nameTagAttachment = net.minecraft.world.phys.Vec3(0.0, 1.0, 0.0)/*[!@]*/
        }
    }
}