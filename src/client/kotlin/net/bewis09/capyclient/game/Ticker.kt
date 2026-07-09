package net.bewis09.capyclient.game

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.util.EventEntrypoint
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents

object Ticker : EventEntrypoint {
    override fun onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register { APIEntrypointLoader.mapEntrypoint { it.getEventEntrypoints().forEach { a -> a.onClientTickStart() } } }
    }
}