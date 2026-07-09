package net.bewis09.capyclient.widget.logic

import com.google.gson.JsonElement
import net.bewis09.capyclient.util.logic.ClientInterface
import net.bewis09.capyclient.widget.Widget

interface WidgetPosition : ClientInterface {
    fun getX(widget: Widget): Float
    fun getY(widget: Widget): Float

    fun saveToJson(): JsonElement

    fun getType(): String

    companion object {
        val types = listOf<WidgetPositionFactory<*>>(
            SidedPosition.Factory, RelativePosition.Factory
        )
    }
}

interface WidgetPositionFactory<T : WidgetPosition> {
    fun createFromJson(jsonElement: JsonElement): T?
    fun getType(): String
}