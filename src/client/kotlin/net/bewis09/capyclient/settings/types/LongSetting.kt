package net.bewis09.capyclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.bewis09.capyclient.util.long

class LongSetting(default: () -> Long) : Setting<Long>(default) {
    override fun convertToElement(): JsonElement? {
        return getWithoutDefault()?.let { JsonPrimitive(it) }
    }

    override fun convertFromElement(data: JsonElement?): Long? = data?.long()
}