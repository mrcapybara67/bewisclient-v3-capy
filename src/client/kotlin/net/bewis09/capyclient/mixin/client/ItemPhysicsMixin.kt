// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.sin

/**
 * Mixes into [ItemEntity] to override the default spinning/floating
 * physics behaviour of dropped items.
 *
 * **Requires [net.bewis09.capyclient.features.utilities.FlatItems] (2D Items) to be enabled.**
 *
 * Vanilla ItemEntity applies a `bob` animation and constant rotation
 * (yaw changes every tick), making items spin in the air.  This mixin
 * provides:
 * - **Lay flat**: Items lie flat on the ground instead of spinning.
 * - **Realistic wobble**: Items wobble as they fall through the air,
 *   with intensity based on fall speed.
 * - **Bounce reduction**: Items barely bounce when hitting the ground,
 *   making them settle quickly.
 *
 * Note: Wobble only applies while the item is in the air (not on ground).
 * Once the item lands, layFlat takes over to keep it flat on the ground.
 * The visual bob is cancelled by dynamically adjusting bobOffset in
 * [net.bewis09.capyclient.mixin.client.FlatItemsMixin].
 */
@Mixin(ItemEntity::class)
abstract class ItemPhysicsMixin {

    @Unique
    private var physicsWobble: Float = 0f

    /**
     * Whether the item was on the ground in the previous tick.
     * Used to detect landing events.
     */
    @Unique
    private var capyclientWasOnGround: Boolean = false

    /**
     * Check if ItemPhysics should be active.
     * ItemPhysics.isEnabled() already checks FlatItems.isEnabled().
     */
    @Unique
    private fun capyclientCanApplyItemPhysics(): Boolean {
        return ItemPhysics.isEnabled()
    }

    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("HEAD")])
    private fun onPreTick(ci: CallbackInfo) {
        if (!capyclientCanApplyItemPhysics()) return
        val self = this as Any as ItemEntity

        // === Detect landing: reduce bounce velocity ===
        if (!capyclientWasOnGround && self.onGround) {
            self.setDeltaMovement(self.deltaMovement.x * 0.8, -0.05, self.deltaMovement.z * 0.8)
        }
        capyclientWasOnGround = self.onGround

        if (ItemPhysics.layFlat.get()) {
            // Reset yaw so items don't spin
            self.yRot = 0f
            self.yRotO = 0f
        }

        // === Wobble (only while falling, not on ground) ===
        if (!self.onGround && ItemPhysics.wobble.get()) {
            // Wobble intensity varies with vertical velocity
            val velY = self.deltaMovement.y
            val fallSpeed = (velY.coerceAtLeast(-1.0).coerceAtMost(0.0) * -10.0f).toFloat()

            // Advance wobble phase based on age
            physicsWobble = (self.age % 120).toFloat() / 120f * 360f

            // Combine phase with fall speed for dynamic wobble
            val wobbleAngle = sin(physicsWobble * 0.15f + fallSpeed) * (3f + fallSpeed * 2f)
            self.xRot = wobbleAngle.coerceIn(-25f, 25f)
            self.xRotO = self.xRot
        } else if (ItemPhysics.layFlat.get()) {
            if (self.onGround) {
                self.xRot = -90f
                self.xRotO = -90f
            } else {
                self.xRot = 0f
                self.xRotO = 0f
            }
        }
    }

    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!capyclientCanApplyItemPhysics()) return
        val self = this as Any as ItemEntity

        // === Reduce ground bounce after vanilla physics ===
        if (self.onGround && self.deltaMovement.y < -0.01) {
            self.setDeltaMovement(
                self.deltaMovement.x,
                self.deltaMovement.y * 0.3,
                self.deltaMovement.z
            )
        }

        if (ItemPhysics.layFlat.get()) {
            self.yRot = 0f
            self.yRotO = 0f
        }

        // === Re-apply wobble after vanilla tick ===
        if (!self.onGround && ItemPhysics.wobble.get()) {
            val velY = self.deltaMovement.y
            val fallSpeed = (velY.coerceAtLeast(-1.0).coerceAtMost(0.0) * -10.0f).toFloat()
            val wobbleAngle = sin(physicsWobble * 0.15f + fallSpeed) * (3f + fallSpeed * 2f)
            self.xRot = wobbleAngle.coerceIn(-25f, 25f)
            self.xRotO = self.xRot
        } else if (ItemPhysics.layFlat.get()) {
            if (self.onGround) {
                self.xRot = -90f
                self.xRotO = -90f
            } else {
                self.xRot = 0f
                self.xRotO = 0f
            }
        }
    }
}
