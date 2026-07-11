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
import org.slf4j.LoggerFactory
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.PI

private val log = LoggerFactory.getLogger("CapyFlatItems")

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
 * The visual bob/hover is neutralised via [FlatItemsRendererMixin]
 * which zeroes the bobOffset in the ItemEntityRenderState during
 * extractRenderState().
 *
 * Ground state is tracked via self-contained Y-position comparison
 * (no @Shadow needed, works across all MC versions).
 *
 * When ItemPhysics.wobble is active, xRot is preserved so the
 * wobble animation still plays — the two features cooperate.
 */
@Mixin(ItemEntity::class)
abstract class FlatItemsMixin {

    /** Self-contained ground tracking - no @Shadow needed. */
    @Unique
    private var capyclientPrevY: Double = Double.MIN_VALUE
    @Unique
    private var capyclientOnGround: Boolean = false
    @Unique
    private var capyclientTickCounter: Int = 0

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
     * Bob offset is zeroed in [FlatItemsRendererMixin] which injects
     * into ItemEntityRenderer.extractRenderState() and overrides
     * the render state's bobOffset to 0 when FlatItems is enabled.
     * This replaces the previous onGetBob() inject that targeted a
     * non-existent getBob(float) method — getBob was never mapped
     * in 1.21.11 Mojang mappings, and the bob is now entirely
     * renderer-side via ItemEntityRenderState.bobOffset.
     */
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
        capyclientTickCounter++
        val shouldLog = capyclientTickCounter % 20 == 0

        if (shouldLog) {
            log.info("[FlatItemsMixin] onPreTick called, FlatItems.isEnabled()={}, ItemPhysics.isEnabled()={}",
                FlatItems.isEnabled(), ItemPhysics.isEnabled())
        }

        if (!FlatItems.isEnabled()) return
        val self = this as Any as ItemEntity

        if (shouldLog) {
            log.info("[FlatItemsMixin] onPreTick PASSED enabled check, self={}, pos=({}, {}, {}), onGround={}, billboard={}",
                self, self.x, self.y, self.z, capyclientOnGround, FlatItems.billboard.get())
        }

        // Track ground state from Y position (no @Shadow needed)
        capyclientTrackGroundState(self)

        // Bob offset is zeroed in FlatItemsRendererMixin via
        // ItemEntityRenderer.extractRenderState(). The old
        // getBob(float) inject was removed because that method
        // doesn't exist in 1.21.11 Mojang mappings.

        val yawBefore = self.yRot
        capyclientFreezeRotation(self)

        if (shouldLog) {
            log.info("[FlatItemsMixin] onPreTick rotation: yRot {} -> {}, xRot {} -> {}, capyclientLastBillboardYaw={}",
                yawBefore, self.yRot, self.xRotO, self.xRot, capyclientLastBillboardYaw)
        }

        // When on the ground, tilt items so they lie flat
        if (capyclientOnGround) {
            self.xRot = -90f
            self.xRotO = -90f
            if (shouldLog) {
                log.info("[FlatItemsMixin] onPreTick SET xRot to -90 (on ground)")
            }
        }
    }

    @Inject(method = ["tick"], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return
        val self = this as Any as ItemEntity

        val shouldLog = capyclientTickCounter % 20 == 0

        // Re-freeze rotation — use cached billboard yaw to avoid atan2 recalc
        if (FlatItems.billboard.get() && capyclientLastBillboardYaw != Float.MAX_VALUE) {
            self.yRot = capyclientLastBillboardYaw
            self.yRotO = capyclientLastBillboardYaw
            if (shouldLog) {
                log.info("[FlatItemsMixin] onPostTick: billboard yaw = {}", capyclientLastBillboardYaw)
            }
        } else {
            self.yRot = 0f
            self.yRotO = 0f
            if (shouldLog) {
                log.info("[FlatItemsMixin] onPostTick: static yaw = 0 (billboard={}, lastYaw={})",
                    FlatItems.billboard.get(), capyclientLastBillboardYaw)
            }
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
            if (shouldLog) {
                log.info("[FlatItemsMixin] onPostTick: keep flat on ground, xRot=-90")
            }
        }
    }
}
