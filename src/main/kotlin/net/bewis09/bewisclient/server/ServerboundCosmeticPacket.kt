package net.bewis09.bewisclient.server

import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketType
import net.minecraft.network.protocol.game.ServerGamePacketListener

class ServerboundCosmeticPacket: Packet<ServerGamePacketListener> {
    override fun type(): PacketType<out Packet<ServerGamePacketListener>> {
        TODO("Not yet implemented")
    }

    override fun handle(listener: ServerGamePacketListener) {
        TODO("Not yet implemented")
    }
}