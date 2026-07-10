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
import kotlin.math.atan2
import kotlin.math.PI

/**
 * Mixes into [ItemEntity] to produce the "2D Items" effect:
 * dropped items no longer spin and optionally always face the
 * player (billboard mode).
 *
 * Billboard mode (default ON):
 *   The item's yaw is calculated each tick so the item always
 *   faces the nearest player — like a real-world billboard.
 *
 * Static mode (billboard OFF):
 *   The item's rotation is frozen at 0 — no spinning, no
 *   billboard, just a static flat sprite on the ground.
 *
 * When ItemPhysics.wobble is active, xRot is preserved so the
 * wobble animation still plays — the two features cooperate.
 */
@Mixin(ItemEntity::class)
abstract class FlatItemsMixin {

    /**
     * Calculates the yaw (in degrees) that would make this item
     * face toward the nearest player.  Returns [Float.MAX_VALUE]
     * when no player is available or the item is on the player's
     * exact position (to avoid atan2(0,0)).
     */
    @Unique
    private fun capyclientBillboardYaw(self: ItemEntity): Float {
        val player = Minecraft.getInstance().player ?: return Float.MAX_VALUE
        val dx = player.x - self.x
        val dz = player.z - self.z
        if (dx == 0.0 && dz == 0.0) return Float.MAX_VALUE
        return (atan2(dz, dx) * 180.0 / PI).toFloat() - 90f
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
                return
            }
        }
        // Static fallback (also used when billboard can't resolve)
        self.yRot = 0f
        self.yRotO = 0f
    }

    @Inject(method = ["tick"], at = [At("HEAD")])
    private fun onPreTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return
        capyclientFreezeRotation(this as Any as ItemEntity)
    }

    @Inject(method = ["tick"], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return
        capyclientFreezeRotation(this as Any as ItemEntity)
    }
}
