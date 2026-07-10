package net.bewis09.capyclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.bewis09.capyclient.util.jsonObject
import net.bewis09.capyclient.widget.logic.WidgetPosition

class WidgetPositionSetting(defaultPos: WidgetPosition) : Setting<WidgetPosition>(defaultPos) {
    override fun convertToElement(): JsonElement? {
        val current = getWithoutDefault() ?: return null

        val jsonObject = JsonObject()

        jsonObject.addProperty("positionType", current.getType())
        jsonObject.add("positionData", current.saveToJson())

        return jsonObject
    }

    override fun convertFromElement(data: JsonElement?): WidgetPosition? {
        val json = (data ?: return null).jsonObject() ?: return null

        if (!data.asJsonObject.has("positionType")) return null
        var value: WidgetPosition? = null

        WidgetPosition.types.forEach {
            if (json.get("positionType")?.asString == it.getType()) {
                it.createFromJson(json.get("positionData"))?.let { a ->
                    value = a
                }
            }
        }

        return value
    }
}