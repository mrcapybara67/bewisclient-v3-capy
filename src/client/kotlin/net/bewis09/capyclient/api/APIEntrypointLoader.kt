package net.bewis09.capyclient.api

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.entrypoint.EntrypointContainer

object APIEntrypointLoader {
    private val entrypoints: List<EntrypointContainer<BewisclientAPIEntrypoint>> by lazy {
        FabricLoader.getInstance().getEntrypointContainers("capyclient", BewisclientAPIEntrypoint::class.java).sortedBy { it.provider.metadata.id?.let { id -> if (id == "capyclient") "" else id } }.map { it }
    }

    fun <T> mapContainer(action: (EntrypointContainer<BewisclientAPIEntrypoint>) -> T): List<T> = entrypoints.map(action)

    fun <T> mapEntrypoint(action: (BewisclientAPIEntrypoint) -> T): List<T> = entrypoints.map { action(it.entrypoint) }

    fun <T> mapEntrypointForList(action: (BewisclientAPIEntrypoint) -> List<T>): List<T> = entrypoints.flatMap { action(it.entrypoint) }
}