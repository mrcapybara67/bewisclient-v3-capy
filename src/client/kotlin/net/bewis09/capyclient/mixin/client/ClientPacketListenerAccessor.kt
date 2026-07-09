package net.bewis09.capyclient.mixin.client

import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.PingDebugMonitor
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(ClientPacketListener::class)
interface ClientPacketListenerAccessor {
    @Accessor("pingDebugMonitor")
    fun getPingDebugMonitor(): PingDebugMonitor
}