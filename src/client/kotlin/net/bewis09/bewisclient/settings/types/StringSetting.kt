package net.bewis09.bewisclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.bewis09.bewisclient.util.string

class StringSetting(default: () -> String) : Setting<String>(default) {
    override fun convertToElement(): JsonElement? {
        return getWithoutDefault()?.let { JsonPrimitive(it) }
    }

    override fun convertFromElement(data: JsonElement?): String? = data?.string()
}