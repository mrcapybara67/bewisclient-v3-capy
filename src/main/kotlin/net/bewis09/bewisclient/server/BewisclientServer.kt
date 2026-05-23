// @VersionReplacement

package net.bewis09.bewisclient.server

import net.bewis09.bewisclient.cosmetics.ClientboundCosmeticPayload
import net.bewis09.bewisclient.cosmetics.ServerboundCosmeticPayload
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

object BewisclientServer: ModInitializer {
    override fun onInitialize() {
        // @[1.21.11] playS2C @[] clientboundPlay
        PayloadTypeRegistry./*[@]*/clientboundPlay/*[!@]*/().register(ClientboundCosmeticPayload.TYPE, ClientboundCosmeticPayload.CODEC)
        // @[1.21.11] playC2S @[] serverboundPlay
        PayloadTypeRegistry./*[@]*/serverboundPlay/*[!@]*/().register(ServerboundCosmeticPayload.TYPE, ServerboundCosmeticPayload.CODEC)

        ServerPlayNetworking.registerGlobalReceiver(ServerboundCosmeticPayload.TYPE) { payload: ServerboundCosmeticPayload, context: ServerPlayNetworking.Context ->

        }
    }
}