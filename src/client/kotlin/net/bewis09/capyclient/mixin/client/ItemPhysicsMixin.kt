// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [ItemEntity] to override the default spinning/floating
 * physics behaviour of dropped items.
 *
 * **Requires [FlatItems] (2D Items) to be enabled** — this module
 * builds on the 2D flat item rendering to add enhanced physics.
 *
 * Vanilla ItemEntity applies a `bob` animation and constant rotation
 * (yaw changes every tick), making items spin in the air.  This mixin
 * resets the rotation so items lie flat on the ground and optionally
 * adds a realistic fall wobble.
 *
 * Note: Wobble only applies while the item is in the air (not on ground).
 * Once the item lands, layFlat takes over to keep it flat on the ground.
 */
@Mixin(ItemEntity::class)
abstract class ItemPhysicsMixin {

    @Unique
    private var physicsWobble: Float = 0f

    /**
     * Check if ItemPhysics should be active.
     * ItemPhysics.isEnabled() already checks FlatItems.isEnabled(),
     * so we only need to check ItemPhysics here.
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

        if (ItemPhysics.layFlat.get()) {
            // Reset yaw so items don't spin.  Vanilla increments yaw
            // by ( age + pickupDelay ) * 1.5 each tick, which gives
            // the spinning effect.  We freeze it at 0.
            self.yRot = 0f
            self.yRotO = 0f
        }

        // Wobble only applies while falling — once on ground, lay flat
        if (!self.onGround && ItemPhysics.wobble.get()) {
            // Simulate a gentle wobble as items fall — varies by age
            // so each item falls slightly differently.
            physicsWobble = (self.age % 60).toFloat() / 60f * 360f
            // Apply a slight tilt that changes over time.
            self.xRot = kotlin.math.sin(physicsWobble * 0.1f) * 5f
            self.xRotO = self.xRot
        } else if (ItemPhysics.layFlat.get()) {
            // Lay-flat: on ground = -90, in air = 0
            if (self.onGround) {
                self.xRot = -90f
                self.xRotO = -90f
            } else {
                self.xRot = 0f
                self.xRotO = 0f
            }
        }
    }

    // Re-apply physics after vanilla tick logic runs.
    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!capyclientCanApplyItemPhysics()) return

        val self = this as Any as ItemEntity

        if (ItemPhysics.layFlat.get()) {
            // Keep the rotation frozen even after vanilla tick resets it.
            self.yRot = 0f
            self.yRotO = 0f
        }

        // Wobble only while falling, flat when on ground
        if (!self.onGround && ItemPhysics.wobble.get()) {
            // Re-apply wobble in case layFlat cleared it.
            self.xRot = kotlin.math.sin(physicsWobble * 0.1f) * 5f
            self.xRotO = self.xRot
        } else if (ItemPhysics.layFlat.get()) {
            // Keep flat
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
