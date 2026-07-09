package net.bewis09.capyclient.widget.logic

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.bewis09.capyclient.features.sidebar.Widgets
import net.bewis09.capyclient.widget.Widget
import net.bewis09.capyclient.widget.WidgetLoader

class RelativePosition(val parent: String, val side: String) : WidgetPosition {
    constructor(parent: Widget, side: String) : this(parent.id.toString(), side)

    val parentWidget by lazy { WidgetLoader.widgets.find { it.id.toString() == parent } }

    override fun getX(widget: Widget): Float {
        val parent = parentWidget ?: return 0f

        if (isInDependencyStack(widget)) return 0f

        if (!parent.isShowing()) {
            return parent.position.get().getX(widget)
        }

        val gap = Widgets.Default.gap.get()

        return when (side) {
            LEFT -> parent.getX() - widget.getScaledWidth() - gap
            RIGHT -> parent.getX() + parent.getScaledWidth() + gap
            TOP -> parent.position.get().getX(widget)
            BOTTOM -> parent.position.get().getX(widget)
            else -> 0f
        }
    }

    override fun getY(widget: Widget): Float {
        val parent = parentWidget ?: return 0f

        if (isInDependencyStack(widget)) return 0f

        if (!parent.isShowing()) {
            return parent.position.get().getY(widget)
        }

        val gap = Widgets.Default.gap.get()

        return when (side) {
            LEFT -> parent.position.get().getY(widget)
            RIGHT -> parent.position.get().getY(widget)
            TOP -> parent.getY() - widget.getScaledHeight() - gap
            BOTTOM -> parent.getY() + parent.getScaledHeight() + gap
            else -> 0f
        }
    }

    fun isInDependencyStack(widget: Widget): Boolean {
        var latest = parentWidget

        while (latest != null && latest != widget) {
            latest = (latest.position.get() as? RelativePosition)?.parentWidget
        }

        return latest == widget
    }

    override fun saveToJson(): JsonElement = JsonObject().also {
        it.addProperty("side", side)
        it.addProperty("parent", parent)
    }

    override fun getType(): String = "relative"

    object Factory : WidgetPositionFactory<RelativePosition> {
        override fun createFromJson(jsonElement: JsonElement): RelativePosition? {
            if (!jsonElement.isJsonObject) return null

            val jsonObject = jsonElement.asJsonObject
            val parent = jsonObject.get("parent")?.asString ?: return null
            val side = jsonObject.get("side")?.asString ?: return null

            return RelativePosition(parent, side)
        }

        override fun getType(): String = "relative"
    }
}

const val TOP = "top"
const val BOTTOM = "bottom"
const val LEFT = "left"
const val RIGHT = "right"