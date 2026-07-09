package net.bewis09.capyclient.widget

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.api.BewisclientAPIEntrypoint
import net.bewis09.capyclient.drawable.renderables.screen.HudEditScreen
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.settings.types.ObjectSetting
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.version.Profiler
import net.bewis09.capyclient.version.registerWidget

/**
 * The entrypoint for the Bewisclient widget events.
 * This is used to register widget-related events in the Bewisclient.
 */
object WidgetLoader : ObjectSetting(), EventEntrypoint {
    val widgets: List<Widget> = APIEntrypointLoader.mapEntrypoint(BewisclientAPIEntrypoint::getWidgets).flatten().apply {
        forEach { create(it.id.toString(), it) }
    }

    override fun onInitializeClient() {
        widgets.forEach {
            registerWidget(
                it.id
            ) { context ->
                Profiler.push("widget_" + it.id.toString().replace(":", "_"))
                if (it.isShowing() && getCurrentRenderableScreen()?.renderable !is HudEditScreen) {
                    it.renderScaled(ScreenDrawing(context, client.font))
                }
                Profiler.pop()
            }
        }
    }

    fun getEnabledWidgets(): List<Widget> {
        return widgets.filter { it.isEnabled() }
    }
}
