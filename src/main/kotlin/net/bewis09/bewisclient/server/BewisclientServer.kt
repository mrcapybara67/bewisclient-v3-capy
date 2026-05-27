// @VersionReplacement

package net.bewis09.bewisclient.server

import net.bewis09.bewisclient.common.logic.ServerInterface
import net.bewis09.bewisclient.cosmetics.ClientboundCosmeticPayload
import net.bewis09.bewisclient.cosmetics.ServerboundCosmeticPayload
import net.bewis09.bewisclient.cosmetics.CommonCosmeticLoader
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object BewisclientServer: ModInitializer, ServerInterface {
    override fun onInitialize() {
//        registerPayloads()
        CommonCosmeticLoader.loadPublicKey()
        CommonCosmeticLoader.loadCosmeticData()

//        ServerPlayNetworking.registerGlobalReceiver(ServerboundCosmeticPayload.TYPE, CommonCosmeticLoader::processC2SPayload)
    }

    fun registerPayloads() {
        // @[1.21.11] playS2C @[] clientboundPlay
        PayloadTypeRegistry./*[@]*/playS2C/*[!@]*/().register(ClientboundCosmeticPayload.TYPE, ClientboundCosmeticPayload.CODEC)
        // @[1.21.11] playC2S @[] serverboundPlay
        PayloadTypeRegistry./*[@]*/playC2S/*[!@]*/().register(ServerboundCosmeticPayload.TYPE, ServerboundCosmeticPayload.CODEC)
    }
}