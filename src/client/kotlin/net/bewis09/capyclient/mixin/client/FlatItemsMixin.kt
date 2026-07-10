package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.FlatItems
import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import kotlin.math.abs
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
 * The visual bob/hover is neutralised via the onGetBob() inject
 * (cancels ItemEntity.getBob(float) if the method exists in Yarn).
 * In 1.21.5+ all bob-related fields (bobOffset, bob, hoverStart)
 * were removed from ItemEntity — only getBob() remains.
 *
 * Ground state is tracked via self-contained Y-position comparison
 * (no @Shadow needed, works across all MC versions).
 *
 * When ItemPhysics.wobble is active, xRot is preserved so the
 * wobble animation still plays — the two features cooperate.
 *
 * NOTE: If getBob(float) is not mapped in Yarn for this version,
 * the bob cancellation is silently skipped (require=0). The main
 * effects (flat rendering, no rotation, billboard) still work.
 */
@Mixin(ItemEntity::class)
abstract class FlatItemsMixin {

    /** Self-contained ground tracking - no @Shadow needed. */
    @Unique
    private var capyclientPrevY: Double = Double.MIN_VALUE
    @Unique
    private var capyclientOnGround: Boolean = false

    @Unique
    private fun capyclientTrackGroundState(self: ItemEntity) {
        capyclientOnGround = self.y == capyclientPrevY || abs(self.deltaMovement.y) < 0.001
        capyclientPrevY = self.y
    }

    @Unique
    private fun mc(): Minecraft = Minecraft.getInstance()

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
        val player = mc().player ?: return Float.MAX_VALUE
        val dx = player.x - self.x
        val dz = player.z - self.z
        if (dx == 0.0 && dz == 0.0) return Float.MAX_VALUE
        return (atan2(dz, dx) * 180.0 / PI).toFloat() - 90f
    }

    /**
     * Injects into ItemEntity.getBob(float) which was added in 1.21.5+
     * as the public getter for the now-removed bobOffset field.
     * Returns 0 to cancel the visual hover entirely.
     * require=0 makes this optional — silently skipped if getBob
     * doesn't exist in the Yarn mapping for this version.
     */
    @Inject(method = ["getBob"], at = [At("HEAD")], cancellable = true, require = 0)
    private fun onGetBob(partialTick: Float, cir: CallbackInfoReturnable<Float>) {
        if (FlatItems.isEnabled()) {
            cir.setReturnValue(0f)
        }
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

        // Track ground state from Y position (no @Shadow needed)
        capyclientTrackGroundState(self)

        // Bob cancellation: onGetBob() inject uses require=0 (best-effort).
        // In Yarn 1.21.11 getBob(float) is not mapped — the sin-based bob
        // animation (~0.1 blocks) may still be visible. See onGetBob().

        capyclientFreezeRotation(self)

        // When on the ground, tilt items so they lie flat
        if (capyclientOnGround) {
            self.xRot = -90f
            self.xRotO = -90f
        }
    }

    @Inject(method = ["tick"], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return
        val self = this as Any as ItemEntity

        // Bob cancellation: onGetBob() inject uses require=0 (best-effort).
        // In Yarn 1.21.11 getBob(float) is not mapped — the sin-based bob
        // animation (~0.1 blocks) may still be visible. See onGetBob().

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
        if (capyclientOnGround) {
            self.xRot = -90f
            self.xRotO = -90f
        }
    }
}
