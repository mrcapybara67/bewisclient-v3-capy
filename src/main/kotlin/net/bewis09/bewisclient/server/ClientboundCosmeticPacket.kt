package net.bewis09.bewisclient.server

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketType
import net.minecraft.network.protocol.game.ClientGamePacketListener

class ClientboundCosmeticPacket: Packet<ClientGamePacketListener> {
    override fun type(): PacketType<out Packet<ClientGamePacketListener>> {
        TODO("Not yet implemented")
    }

    override fun handle(listener: ClientGamePacketListener) {
        TODO("Not yet implemented")
    }
}