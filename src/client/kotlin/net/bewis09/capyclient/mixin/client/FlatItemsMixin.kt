package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.FlatItems
import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.atan2
import kotlin.math.PI

/**
 * Mixes into [ItemEntity] to produce the "2D Items" effect:
 * dropped items no longer spin, lie flat on the ground,
 * and optionally always face the player (billboard mode).
 *
 * Billboard mode (default ON):
 *   The item's yaw is calculated each tick so the item always
 *   faces the nearest player — like a real-world billboard.
 *
 * Static mode (billboard OFF):
 *   The item's rotation is frozen at 0 — no spinning, no
 *   billboard, just a static flat sprite on the ground.
 *
 * The visual bob/hover is neutralised by dynamically adjusting
 * bobOffset so that sin((age + partialTick) / 10 + bobOffset) = -1,
 * which makes the bob amplitude 0 (items sit at ground level).
 *
 * When ItemPhysics.wobble is active, xRot is preserved so the
 * wobble animation still plays — the two features cooperate.
 */
@Mixin(ItemEntity::class)
abstract class FlatItemsMixin {

    @Shadow
    private var bobOffset: Float = 0f

    @Unique
    private val mc: Minecraft get() = Minecraft.getInstance()

    /**
     * Cached yaw value to avoid recalculating billboard in postTick.
     */
    @Unique
    private var capyclientLastBillboardYaw: Float = Float.MAX_VALUE

    /**
     * Calculates the yaw (in degrees) that would make this item
     * face toward the nearest player.  Returns [Float.MAX_VALUE]
     * when no player is available.
     */
    @Unique
    private fun capyclientBillboardYaw(self: ItemEntity): Float {
        val player = mc.player ?: return Float.MAX_VALUE
        val dx = player.x - self.x
        val dz = player.z - self.z
        if (dx == 0.0 && dz == 0.0) return Float.MAX_VALUE
        return (atan2(dz, dx) * 180.0 / PI).toFloat() - 90f
    }

    @Unique
    private fun capyclientUpdateBobOffset(self: ItemEntity) {
        // Vanilla bob formula: bob = sin((age + partialTick) / 10 + bobOffset) * 0.1 + 0.1
        // To make bob = 0: sin(...) = -1, so (age + partialTick)/10 + bobOffset = -PI/2
        // Using partialTick ≈ 0.5 as the midpoint of the frame interpolation:
        // bobOffset = -PI/2 - (age + 0.5) / 10
        // This cancels the bob for any partialTick value because sin(x) ≈ -1 for x ≈ -PI/2
        bobOffset = (-PI / 2.0 - (self.age + 0.5) / 10.0).toFloat()
    }

    @Unique
    private fun capyclientFreezeRotation(self: ItemEntity) {
        // Preserve xRot when ItemPhysics wobble is active
        if (!ItemPhysics.isEnabled() || !ItemPhysics.wobble.get()) {
            self.xRot = 0f
            self.xRotO = 0f
        }

        if (FlatItems.billboard.get()) {
            val yaw = capyclientBillboardYaw(self)
            if (yaw != Float.MAX_VALUE) {
                self.yRot = yaw
                self.yRotO = yaw
                capyclientLastBillboardYaw = yaw
                return
            }
        }
        // Static fallback
        self.yRot = 0f
        self.yRotO = 0f
    }

    @Inject(method = ["tick"], at = [At("HEAD")])
    private fun onPreTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return
        val self = this as Any as ItemEntity

        // Dynamically adjust bobOffset to cancel the visual hover
        capyclientUpdateBobOffset(self)

        capyclientFreezeRotation(self)

        // When on the ground, tilt items so they lie flat
        if (self.onGround) {
            self.xRot = -90f
            self.xRotO = -90f
        }
    }

    @Inject(method = ["tick"], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return
        val self = this as Any as ItemEntity

        // Re-apply bob offset cancellation after vanilla tick
        capyclientUpdateBobOffset(self)

        // Re-freeze rotation — use cached billboard yaw to avoid atan2 recalc
        if (FlatItems.billboard.get() && capyclientLastBillboardYaw != Float.MAX_VALUE) {
            self.yRot = capyclientLastBillboardYaw
            self.yRotO = capyclientLastBillboardYaw
        } else {
            self.yRot = 0f
            self.yRotO = 0f
        }

        // Preserve xRot when ItemPhysics wobble is active
        if (!ItemPhysics.isEnabled() || !ItemPhysics.wobble.get()) {
            self.xRot = 0f
            self.xRotO = 0f
        }

        // Keep items flat on the ground
        if (self.onGround) {
            self.xRot = -90f
            self.xRotO = -90f
        }
    }
}
