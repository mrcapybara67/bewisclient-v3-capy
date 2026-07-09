package net.bewis09.capyclient

import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.util.logic.ClientInterface
import net.fabricmc.api.ClientModInitializer

object BewisclientInitializer : ClientInterface, ClientModInitializer {
    override fun onInitializeClient() {
        EventEntrypoint.registerEntrypoints()
        EventEntrypoint.onAllEventEntrypoints(EventEntrypoint::onInitializeClient)
    }
}