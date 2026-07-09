@file:Suppress("PropertyName")

package net.bewis09.capyclient.cosmetics

import net.bewis09.capyclient.common.createIdentifier
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.UUID

class ClientboundCosmeticPayload(val playerUuid: UUID, val cosmetics: Map<String, String>): CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ClientboundCosmeticPayload>(createIdentifier("capyclient", "cosmetic_packet"))
        val CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, ClientboundCosmeticPayload::playerUuid, ByteBufCodecs.map({ mutableMapOf() }, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), ClientboundCosmeticPayload::cosmetics, ::ClientboundCosmeticPayload)
    }

    override fun type() = TYPE
}