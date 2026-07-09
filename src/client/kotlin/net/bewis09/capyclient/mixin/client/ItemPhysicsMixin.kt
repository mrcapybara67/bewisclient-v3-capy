// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [ItemEntity] to override the default spinning/floating
 * physics behaviour of dropped items.
 *
 * Vanilla ItemEntity applies a `bob` animation and constant rotation
 * (yaw changes every tick), making items spin in the air.  This mixin
 * resets the rotation so items lie flat on the ground and optionally
 * adds a realistic fall wobble.
 */
@Mixin(ItemEntity::class)
abstract class ItemPhysicsMixin {

    @Shadow
    private var age: Int = 0

    @Shadow
    private var pickupDelay: Int = 0

    @Unique
    private var physicsWobble: Float = 0f

    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("HEAD")])
    private fun onPreTick(ci: CallbackInfo) {
        if (!ItemPhysics.isEnabled()) return

        val self = this as ItemEntity

        if (ItemPhysics.layFlat.get()) {
            // Reset yaw so items don't spin.  Vanilla increments yaw
            // by ( age + pickupDelay ) * 1.5 each tick, which gives
            // the spinning effect.  We freeze it at 0.
            self.yRot = 0f
            self.yRotO = 0f
            self.xRot = 0f
            self.xRotO = 0f
        }

        if (ItemPhysics.wobble.get()) {
            // Simulate a gentle wobble as items fall — varies by age
            // so each item falls slightly differently.
            physicsWobble = (self.age % 60).toFloat() / 60f * 360f
            // Apply a slight tilt that changes over time.
            self.xRot = kotlin.math.sin(physicsWobble * 0.1f) * 5f
        }
    }

    // Re-apply physics after vanilla tick logic runs.
    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!ItemPhysics.isEnabled()) return

        val self = this as ItemEntity

        if (ItemPhysics.layFlat.get()) {
            // Keep the rotation frozen even after vanilla tick resets it.
            self.yRot = 0f
            self.yRotO = 0f
            self.xRot = 0f
            self.xRotO = 0f
        }
    }
}