package net.bewis09.capyclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import net.bewis09.capyclient.util.boolean
import net.bewis09.capyclient.util.float
import net.bewis09.capyclient.util.int
import net.bewis09.capyclient.util.string
import net.bewis09.capyclient.util.jsonObject

open class MapSetting<T>(val from: (JsonElement) -> T?, val to: (T) -> JsonElement) : Setting<HashMap<String, T>>(hashMapOf()) {
    override fun convertToElement(): JsonElement {
        return get().let { map ->
            JsonObject().also {
                map.forEach { (key, value) ->
                    it.add(key, to(value))
                }
            }
        }
    }

    operator fun get(key: String): T? {
        return get()[key]
    }

    operator fun get(key: String, default: T): T {
        return this[key] ?: default
    }

    operator fun set(key: String, value: T?) {
        val currentMap = get()
        if (value == null) {
            currentMap.remove(key)
        } else {
            currentMap[key] = value
        }
        save()
    }

    override fun convertFromElement(data: JsonElement?): HashMap<String, T>? {
        val obj = data?.jsonObject() ?: return null
        val result = HashMap<String, T>()
        for (entry in obj.entrySet()) {
            val value = from(entry.value) ?: continue
            result[entry.key] = value
        }
        return result
    }
}

open class BooleanMapSetting : MapSetting<Boolean>(from = { it.boolean() }, to = { JsonPrimitive(it) })

open class IntegerMapSetting : MapSetting<Int>(from = { it.int() }, to = { JsonPrimitive(it) })

open class StringMapSetting : MapSetting<String>(from = { it.string() }, to = { JsonPrimitive(it) })

open class FloatMapSetting : MapSetting<Float>(from = { it.float() }, to = { JsonPrimitive(it) })