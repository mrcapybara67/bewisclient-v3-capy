package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.FlatItems
import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.minecraft.world.entity.item.ItemEntity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [ItemEntity] to freeze dropped-item rotation when
 * FlatItems is enabled, producing a flat static "2D Items" look.
 *
 * Vanilla ItemEntity spins by incrementing yaw every tick based on age
 * and pickup-delay.  This mixin freezes the rotation at 0 so items
 * appear as flat, non-spinning sprites.
 *
 * When ItemPhysics.wobble is active, xRot is preserved (not frozen)
 * so the wobble animation still plays — the two features cooperate.
 */
@Mixin(ItemEntity::class)
abstract class FlatItemsMixin {

    @Inject(method = ["tick"], at = [At("HEAD")])
    private fun onPreTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return

        val self = this as Any as ItemEntity

        // Freeze yaw to prevent vanilla spinning.
        // Preserve xRot when ItemPhysics wobble is active.
        if (!ItemPhysics.isEnabled() || !ItemPhysics.wobble.get()) {
            self.xRot = 0f
            self.xRotO = 0f
        }
        self.yRot = 0f
        self.yRotO = 0f
    }

    @Inject(method = ["tick"], at = [At("RETURN")])
    private fun onPostTick(ci: CallbackInfo) {
        if (!FlatItems.isEnabled()) return

        val self = this as Any as ItemEntity

        // Re-freeze after vanilla tick re-applies rotation.
        if (!ItemPhysics.isEnabled() || !ItemPhysics.wobble.get()) {
            self.xRot = 0f
            self.xRotO = 0f
        }
        self.yRot = 0f
        self.yRotO = 0f
    }
}
