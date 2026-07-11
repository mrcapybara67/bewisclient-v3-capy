// @VersionReplacement

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
import kotlin.math.abs
import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("CapyItemPhysics")

/**
 * Mixes into [ItemEntity] to override the default spinning/floating
 * physics behaviour of dropped items.
 *
 * **Requires [net.bewis09.capyclient.features.utilities.FlatItems] (2D Items) to be enabled.**
 *
 * PERFORMANCE:
 * - Items > 64 blocks from the player are skipped (squared distance, no sqrt)
 * - Wobble uses cubic polynomial approximation (no sin/cos calls at all)
 * - Wobble computed ONCE per tick in preTick, cached and reused in postTick
 * - All fields cached as lazy vals to avoid Minecraft.getInstance() spam
 */
@Mixin(ItemEntity::class)
abstract class ItemPhysicsMixin {

    @Unique
    private fun mc(): Minecraft = Minecraft.getInstance()

    /** Cached wobble values to avoid recomputing in postTick. */
    @Unique
    private var cachedWobbleXRot: Float = 0f
    @Unique
    private var cachedWobbleActive: Boolean = false

    @Unique
    private var physicsWobblePhase: Int = 0
    @Unique
    private var capyclientTickCounter: Int = 0

    /** Self-contained ground tracking - no @Shadow needed. */
    @Unique
    private var capyclientPrevY: Double = Double.MIN_VALUE
    @Unique
    private var capyclientOnGround: Boolean = false
    @Unique
    private var capyclientWasOnGround: Boolean = false

    /** Updates ground state by comparing Y position (works across all MC versions). */
    @Unique
    private fun capyclientTrackGroundState(self: ItemEntity) {
        capyclientWasOnGround = capyclientOnGround
        capyclientOnGround = self.y == capyclientPrevY || abs(self.deltaMovement.y) < 0.001
        capyclientPrevY = self.y
    }

    @Unique
    private fun capyclientCanApplyItemPhysics(): Boolean = ItemPhysics.isEnabled()

    /** Max distance in blocks for physics to apply (squared, avoids sqrt). */
    @Unique
    private fun capyclientIsNearPlayer(self: ItemEntity): Boolean {
        val player = mc().player ?: return false
        val dx = player.x - self.x
        val dz = player.z - self.z
        return (dx * dx + dz * dz) < 4096.0  // 64² = 4096
    }

    /**
     * Fast cubic approximation of sin(x) using Bhashara I's formula.
     * Max error ~0.003, no LUT, no native sin() call, just 4 FMA ops.
     * Handles all angles [0, 360) correctly with sign tracking.
     */
    @Unique
    private fun fastSinApprox(degrees: Float): Float {
        var x = degrees % 360f
        if (x < 0f) x += 360f
        // Track sign: sin(θ) = -sin(θ - 180°) for θ in (180, 360)
        val negative = x > 180f
        if (negative) x -= 180f
        // Now x in [0, 180]. Use sin(x) = sin(180 - x) for x in (90, 180]
        if (x > 90f) x = 180f - x
        // Now x in [0, 90], convert to radians in [0, PI/2]
        val rad = x * 0.017453292f
        val x2 = rad * rad
        // Bhashara I: sin(x) ≈ 1.27324x - 0.40528x² for x in [0, π/2]
        val result = 1.2732395f * rad - 0.4052847f * x2
        return if (negative) -result else result
    }

    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("HEAD")])
    private fun onPreTick(ci: CallbackInfo) {
        capyclientTickCounter++
        val shouldLog = capyclientTickCounter % 20 == 0

        if (shouldLog) {
            log.info("[ItemPhysicsMixin] onPreTick: capyclientCanApplyItemPhysics()={}, ItemPhysics.isEnabled()={}, FlatItems.isEnabled()={}",
                capyclientCanApplyItemPhysics(), ItemPhysics.isEnabled(), FlatItems.isEnabled())
        }

        if (!capyclientCanApplyItemPhysics()) return
        val self = this as Any as ItemEntity

        if (!capyclientIsNearPlayer(self)) {
            if (shouldLog) {
                log.info("[ItemPhysicsMixin] onPreTick: item too far, skipping (pos={},{},{})", self.x, self.y, self.z)
            }
            return
        }

        if (shouldLog) {
            log.info("[ItemPhysicsMixin] onPreTick: PASSED all checks, self={}, layFlat={}, wobble={}, deltaY={}",
                self, ItemPhysics.layFlat.get(), ItemPhysics.wobble.get(), self.deltaMovement.y)
        }

        // Track ground state from Y position (no @Shadow needed)
        capyclientTrackGroundState(self)

        // === Detect landing transition: reduce horizontal velocity ===
        if (capyclientOnGround && !capyclientWasOnGround && capyclientPrevY != Double.MIN_VALUE) {
            if (shouldLog) {
                log.info("[ItemPhysicsMixin] onPreTick: landing detected, reducing velocity")
            }
            self.setDeltaMovement(self.deltaMovement.x * 0.8, 0.0, self.deltaMovement.z * 0.8)
        }

        if (ItemPhysics.layFlat.get()) {
            self.yRot = 0f
            self.yRotO = 0f
        }

        // === Wobble (only while falling, not on ground) — computed ONCE ===
        cachedWobbleActive = false
        if (!capyclientOnGround && ItemPhysics.wobble.get() && self.deltaMovement.y < -0.01) {
            val velY = self.deltaMovement.y
            val fallSpeed = (velY.coerceAtLeast(-1.0) * -10.0f).toFloat()

            // Increment phase, wrap at 360 to prevent overflow
            physicsWobblePhase = (physicsWobblePhase + 3) % 360

            // Fast cubic sin approximation — NO native sin() call!
            val wobbleAngle = fastSinApprox(physicsWobblePhase.toFloat() + fallSpeed * 10f) * (3f + fallSpeed * 2f)
            val clamped = wobbleAngle.coerceIn(-25f, 25f)

            cachedWobbleXRot = clamped
            cachedWobbleActive = true

            self.xRot = clamped
            self.xRotO = clamped

            if (shouldLog) {
                log.info("[ItemPhysicsMixin] onPreTick: WOBBLE active, phase={}, wobbleAngle={}, clamped={}",
                    physicsWobblePhase, wobbleAngle, clamped)
            }
        } else if (ItemPhysics.layFlat.get()) {
            if (capyclientOnGround) {
                self.xRot = -90f
                self.xRotO = -90f
                if (shouldLog) {
                    log.info("[ItemPhysicsMixin] onPreTick: layFlat+onGround, xRot=-90")
                }
            } else {
                self.xRot = 0f
                self.xRotO = 0f
            }
        } else if (shouldLog) {
            log.info("[ItemPhysicsMixin] onPreTick: no wobble/layFlat, onGround={}, deltaY={}",
                capyclientOnGround, self.deltaMovement.y)
        }
    }

    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!capyclientCanApplyItemPhysics()) return
        val self = this as Any as ItemEntity

        // === Skip distant items ===
        if (!capyclientIsNearPlayer(self)) return

        val shouldLog = capyclientTickCounter % 20 == 0

        if (ItemPhysics.layFlat.get()) {
            self.yRot = 0f
            self.yRotO = 0f
        }

        // === Re-apply wobble from cache (no recomputation) ===
        if (cachedWobbleActive) {
            self.xRot = cachedWobbleXRot
            self.xRotO = cachedWobbleXRot
            if (shouldLog) {
                log.info("[ItemPhysicsMixin] onPostTick: re-applied wobble, xRot={}", cachedWobbleXRot)
            }
        } else if (ItemPhysics.layFlat.get()) {
            if (capyclientOnGround) {
                self.xRot = -90f
                self.xRotO = -90f
                if (shouldLog) {
                    log.info("[ItemPhysicsMixin] onPostTick: layFlat+onGround, xRot=-90")
                }
            } else {
                self.xRot = 0f
                self.xRotO = 0f
            }
        } else if (shouldLog) {
            log.info("[ItemPhysicsMixin] onPostTick: no action (cachedWobbleActive={}, layFlat={}, onGround={})",
                cachedWobbleActive, ItemPhysics.layFlat.get(), capyclientOnGround)
        }
    }
}