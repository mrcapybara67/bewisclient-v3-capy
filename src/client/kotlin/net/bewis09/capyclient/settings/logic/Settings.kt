package net.bewis09.capyclient.settings.logic

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.settings.types.ObjectSetting
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.util.logic.ClientInterface

object Settings : ObjectSetting(), ClientInterface, EventEntrypoint {
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    var isLoading = true
    var dirty: Boolean = false

    init {
        for (feature in APIEntrypointLoader.mapEntrypoint { it.getOtherSettings() + it.getUtilities() + it.getSidebarCategories() }.flatten()) {
            create(if (feature.id.namespace == "capyclient") feature.id.path else feature.id.toString(), feature)
        }
    }

    fun load() {
        if (Version2Migration.update()) {
            saveAll()
        }

        isLoading = true
        val data = readRelativeFile("capyclient", "capyclient.json")
        setFromElement(gson.fromJson(data, JsonElement::class.java))
        isLoading = false
    }

    fun setDirty() {
        dirty = true
    }

    fun saveAll() {
        if (dirty && !isLoading) {
            val jsonElement = convertToElement()
            val jsonString = gson.toJson(jsonElement)
            saveRelativeFile(jsonString, "capyclient", "capyclient.json")
            dirty = false
        }
    }

    override fun onInitializeClient() = load()

    override fun onClientTickStart() = saveAll()
}