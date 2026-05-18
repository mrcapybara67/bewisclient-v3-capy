package net.bewis09.bewisclient.server

import net.bewis09.bewisclient.common.createIdentifier
import net.fabricmc.api.ModInitializer
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.PacketType
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ServerGamePacketListener

object BewisclientServer: ModInitializer {
    override fun onInitialize() {

    }

    private fun <T : Packet<ClientGamePacketListener>> createClientbound(id: String): PacketType<T> {
        return PacketType(PacketFlow.CLIENTBOUND, createIdentifier("bewisclient", id))
    }

    private fun <T : Packet<ServerGamePacketListener>> createServerbound(id: String): PacketType<T> {
        return PacketType(PacketFlow.SERVERBOUND, createIdentifier("bewisclient", id))
    }
}