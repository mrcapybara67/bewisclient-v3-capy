@file:Suppress("PropertyName")

package net.bewis09.bewisclient.cosmetics

import net.bewis09.bewisclient.common.createIdentifier
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class ServerboundCosmeticPayload(val cosmetics: Map<String, String>): CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<ServerboundCosmeticPayload>(createIdentifier("bewisclient", "cosmetic_packet"))
        val CODEC = StreamCodec.composite(ByteBufCodecs.map({ mutableMapOf() }, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), ServerboundCosmeticPayload::cosmetics, ::ServerboundCosmeticPayload)
    }

    override fun type() = TYPE
}