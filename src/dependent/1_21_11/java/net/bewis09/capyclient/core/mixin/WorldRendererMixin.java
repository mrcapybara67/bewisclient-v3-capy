package net.bewis09.capyclient.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.bewis09.capyclient.features.utilities.BlockHighlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Redirect(method = "renderHitOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ShapeRenderer;renderShape(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/shapes/VoxelShape;DDDIF)V"))
    private void drawOutline(PoseStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double x, double y, double z, int c, float lineWidth) {
        if (!BlockHighlight.INSTANCE.isEnabled()) {
            ShapeRenderer.renderShape(matrixStack, vertexConsumer, voxelShape, x, y, z, c, lineWidth);
            return;
        }

        Minecraft client = Minecraft.getInstance();

        // 'onlyWhenSneaking' short-circuits the entire draw call (not just the colour).
        // If we just changed the colour to a fully transparent int, vanilla's outline shader
        // would still burn the draw call. Returning the unmodified render avoids the per-frame
        // buffer submit.
        if (BlockHighlight.INSTANCE.getOnlyWhenSneaking().get()) {
            var player = client.player;
            if (player == null || !player.isShiftKeyDown()) {
                return;
            }
        }

        // 'range' gate. The hit-position is not in this method's signature, so we
        // approximate the distance to the targeted block via the hitResult. If
        // there is no hitResult, we still draw (e.g. hovering entity → vanilla
        // wouldn't draw the outline anyway, but we keep symmetry).
        //
        // The 1.21.11 `Camera.getPosition()` API name doesn't resolve in this
        // build's mapping, so we use the player's eye position instead. The
        // eye position is at the camera in first-person and very close in
        // third-person — the range gate is a coarse "is the target near the
        // player?" check, so the small offset doesn't matter.
        float range = BlockHighlight.INSTANCE.getRange().get();
        var hit = client.hitResult;
        if (hit != null && client.player != null) {
            var eye = client.player.getEyePosition();
            double dx = hit.getLocation().x - eye.x;
            double dy = hit.getLocation().y - eye.y;
            double dz = hit.getLocation().z - eye.z;
            if ((dx * dx + dy * dy + dz * dz) > (double) (range * range)) {
                return;
            }
        }

        // Pulse animation: scale lineWidth by a sin wave centred at 0.75x–1.0x
        // of the user's thickness setting. sin(t) is in [-1, 1] so 0.875 + 0.125*sin
        // maps to [0.75, 1.0] which keeps the outline visible at all phases.
        float thickness = BlockHighlight.INSTANCE.getThickness().get();
        if (BlockHighlight.INSTANCE.getPulse().get()) {
            float speed = BlockHighlight.INSTANCE.getPulseSpeed().get();
            double t = System.currentTimeMillis() / 1000.0 * speed;
            float wave = (float) (0.875 + 0.125 * Math.sin(t * 2.0 * Math.PI));
            thickness *= wave;
        }

        // CRITICAL FIX: the previous build did `(int)(thickness*255) << 24` which
        // produces a NEGATIVE int for thickness > ~0.5 (because bit 31 is set
        // after the shift). A negative int OR'd with 0x00FFFFFF keeps the sign
        // bit, which can flip the colour in some blends. Mask to 8 bits first
        // so the shift always yields a clean 0..255 alpha byte.
        int alphaByte = Math.max(0, Math.min(255, (int) (thickness * 255f))) & 0xFF;
        int color = (BlockHighlight.INSTANCE.getColor().get().getColorInt() & 0x00FFFFFF) | (alphaByte << 24);

        ShapeRenderer.renderShape(matrixStack, vertexConsumer, voxelShape, x, y, z, color, lineWidth);
    }
}
