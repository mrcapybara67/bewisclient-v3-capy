package net.bewis09.bewisclient.settings.logic

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import net.bewis09.bewisclient.settings.types.Setting
import net.bewis09.bewisclient.util.logic.ClientInterface

abstract class Settings : ClientInterface {
    companion object {
        val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    var isLoading = true
    var dirty: Boolean = false

    /**
     * Returns the ID of the settings.
     * It is used to name the settings file.
     */
    abstract fun getId(): String

    abstract fun getMainSetting(): Setting<*>?

    open fun load() {
        isLoading = true
        val data = readRelativeFile("bewisclient", getId() + ".json")
        getMainSetting()?.setFromElement(gson.fromJson(data, JsonElement::class.java))
        isLoading = false
    }

    fun setDirty() {
        dirty = true
    }

    fun save() {
        if (dirty && !isLoading) {
            val mainSetting = getMainSetting() ?: return
            val jsonElement = mainSetting.convertToElement()
            val jsonString = gson.toJson(jsonElement)
            saveRelativeFile(jsonString, "bewisclient", getId() + ".json")
            dirty = false
        }
    }
}