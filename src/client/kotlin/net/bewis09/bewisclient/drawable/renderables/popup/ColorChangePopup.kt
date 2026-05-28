package net.bewis09.bewisclient.drawable.renderables.popup

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.bewisclient.drawable.renderables.components.Rectangle
import net.bewis09.bewisclient.drawable.renderables.components.ThemeButton
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.settings.impl.GeneralSettings
import net.bewis09.bewisclient.util.color.ColorSaver
import net.bewis09.bewisclient.util.interfaces.Gettable

class ColorChangePopup(val state: Gettable<ColorSaver>, val onChange: (ColorSaver) -> Unit, val types: Array<String>) : Renderable(200, 100) {
    val buttons = types.map { type ->
        ColorSaver.getType(type)?.let {
            ThemeButton(it.getTranslation()(), {
                state.get().getType() == type
            }, { _ ->
                if (state.get().getType() != type) {
                    onChange(it.getDefault())
                    renderables.clear()
                    init()
                }
            }, it.getDescription()?.invoke())
        }
    }

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        SelectiveScreenDrawer.renderPopupBackground(screenDrawing, x, y, width, height, 5, 0.3f)
        renderRenderables(screenDrawing, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        buttons.forEachIndexed { index, button ->
            button?.let {
                it.setSize((width - ((buttons.size - 1) * 5) - 10) / buttons.size, 14)
                it.setPosition(x + 5 + index * (it.width + 5), y + height - 20)
                addRenderable(it)
            }
        }
        addRenderable(Rectangle { GeneralSettings.getThemeColor(alpha = 0.3f) }(x + 5, y + height - 26, width - 11, 1))
        ColorSaver.getFactory(state.get())?.getSettingsRenderable({ state.get() }, onChange)(x + 5, y + 6, width - 11, height - 37)?.let { addRenderable(it); it.resize() }
    }
}