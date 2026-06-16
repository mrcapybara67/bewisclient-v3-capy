package net.bewis09.bewisclient.drawable.renderables.popup

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.bewisclient.drawable.renderables.components.button.Button
import net.bewis09.bewisclient.drawable.renderables.components.element.TextElement
import net.bewis09.bewisclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.bewisclient.drawable.renderables.impl.TiwylaLinesSettingsRenderable
import net.bewis09.bewisclient.drawable.renderables.screen.OptionScreen
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.types.ListSetting
import net.bewis09.bewisclient.widget.impl.TiwylaWidget

class TiwylaLinesSettingsPopup<T>(
    val setting: ListSetting<TiwylaWidget.Information<T>>, val options: List<TiwylaWidget.Line<T>>, val yIndex: Int, val left: Boolean
) : Renderable() {
    companion object {
        val selectText = Translation("popup.tiwyla_lines_settings.title", "Select Information")
    }

    val plane = VerticalAlignScrollPlane(
        {
            mutableListOf(Button(TiwylaLinesSettingsRenderable.none()) {
                if (yIndex < setting.size) {
                    val arr = arrayOf(setting[yIndex].first, setting[yIndex].second).filterNotNull().sortedBy { a -> a.priority }
                    setting[yIndex] = TiwylaWidget.Information(
                        if (left) null else arr.getOrNull(0), if (left) arr.getOrNull(1) else null
                    )
                }

                OptionScreen.currentInstance?.closePopup()
                OptionScreen.currentInstance?.resize()
            }.setHeight(SelectiveScreenDrawer.getSideButtonHeight())).also {
                it += options.map { option ->
                    Button(option.translation()) {
                        if (yIndex >= setting.size) {
                            setting.add(TiwylaWidget.Information(option, null))
                        } else {
                            val arr = arrayOf(setting[yIndex].first, setting[yIndex].second).filterNotNull().sortedBy { a -> a.priority }
                            setting[yIndex] = TiwylaWidget.Information(
                                if (left) option else arr.getOrNull(0), if (left) arr.getOrNull(1) else option
                            )
                        }

                        OptionScreen.currentInstance?.closePopup()
                        OptionScreen.currentInstance?.resize()
                    }.setHeight(SelectiveScreenDrawer.getSideButtonHeight())
                }
            }
        }, 2
    )

    init {
        internalWidth = 200
        internalHeight = 100
    }

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        SelectiveScreenDrawer.renderPopupBackground(screenDrawing, x, y, width, height, 5, 0.3f)
        renderRenderables(screenDrawing, mouseX, mouseY)
    }

    override fun init() {
        addRenderable(TextElement(selectText(), centered = true)(x, y + 6, width, 14))
        addRenderable(plane(x + 5, y + 25, width - 10, height - 30))
    }
}