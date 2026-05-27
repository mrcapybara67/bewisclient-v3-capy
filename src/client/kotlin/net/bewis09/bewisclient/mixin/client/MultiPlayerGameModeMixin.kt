package net.bewis09.bewisclient.mixin.client

import net.minecraft.client.multiplayer.MultiPlayerGameMode
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(MultiPlayerGameMode::class)
interface MultiPlayerGameModeMixin {
    @Accessor("destroyProgress")
    fun getDestroyProgress(): Float
}