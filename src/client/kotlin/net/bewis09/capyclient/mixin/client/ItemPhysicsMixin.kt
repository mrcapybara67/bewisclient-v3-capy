// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import kotlin.math.abs

/**
 * Mixes into [ItemEntity] to override the default hovering/spinning
 * behaviour of dropped items.
 *
 * When enabled:
 * - Items fall to the ground with gravity (no artificial hovering).
 * - Items spin/rotate while falling, at a speed controlled by the
 *   Rotation Speed setting.
 * - Once an item lands, it comes to rest lying flat on the ground
 *   instead of continuing to spin.
 *
 * Ground detection is self-contained (no @Shadow) and uses the same
 * Y-position comparison used by the previous implementation.
 */
@Mixin(ItemEntity::class)
abstract class ItemPhysicsMixin {

    @Unique
    private var capyclientPrevY: Double = Double.MIN_VALUE

    @Unique
    private var capyclientOnGround: Boolean = false

    @Unique
    private var capyclientWasOnGround: Boolean = false

    @Unique
    private val mc: Minecraft = Minecraft.getInstance()

    @Unique
    private var capyclientIsNearPlayerResult: Boolean = false

    /**
     * Max distance in blocks for physics to apply (squared, avoids sqrt).
     * Items far from the player keep vanilla behaviour.
     */
    @Unique
    private fun capyclientIsNearPlayer(self: ItemEntity): Boolean {
        val player = mc.player ?: return false
        val dx = player.x - self.x
        val dz = player.z - self.z
        return (dx * dx + dz * dz) < 4096.0 // 64²
    }

    /** Updates ground state by comparing Y position (works across all MC versions). */
    @Unique
    private fun capyclientTrackGroundState(self: ItemEntity) {
        capyclientWasOnGround = capyclientOnGround
        capyclientOnGround = self.y == capyclientPrevY || abs(self.deltaMovement.y) < 0.001
        capyclientPrevY = self.y
    }

    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("HEAD")])
    private fun onPreTick(ci: CallbackInfo) {
        if (!ItemPhysics.isEnabled()) return
        val self = this as Any as ItemEntity

        capyclientIsNearPlayerResult = capyclientIsNearPlayer(self)
        if (!capyclientIsNearPlayerResult) return

        capyclientTrackGroundState(self)

        // Reduce horizontal sliding once an item has landed.
        if (capyclientOnGround && !capyclientWasOnGround && capyclientPrevY != Double.MIN_VALUE) {
            self.setDeltaMovement(self.deltaMovement.x * 0.8, 0.0, self.deltaMovement.z * 0.8)
        }
    }

    // @[1.21.11] "tick" @[] "onEntityTick"
    @Inject(method = [/*[@]*/"tick"/*[!@]*/], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!ItemPhysics.isEnabled()) return
        val self = this as Any as ItemEntity

        if (!capyclientIsNearPlayerResult) return

        if (capyclientOnGround) {
            // Lay flat on the ground.
            self.xRot = -90f
            self.xRotO = -90f
            self.yRot = 0f
            self.yRotO = 0f
        } else {
            // Spin while falling.
            val speed = ItemPhysics.rotationSpeed.get()
            self.xRot += 8f * speed
            self.yRot += 8f * speed
            self.xRot %= 360f
            self.yRot %= 360f
        }
    }
}