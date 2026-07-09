package net.bewis09.capyclient.drawable.renderables.popup

import net.bewis09.capyclient.common.setColor
import net.bewis09.capyclient.common.toText
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.capyclient.drawable.renderables.screen.PopupScreen
import net.bewis09.capyclient.drawable.renderables.settings.InfoTextRenderable
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.features.sidebar.General
import net.bewis09.capyclient.widget.impl.CustomWidget

class CustomWidgetHelpPopup(val screen: PopupScreen) : Renderable() {
    init {
        internalWidth = 200
        internalHeight = screenHeight - 100
    }

    val plane = VerticalAlignScrollPlane(
        mutableListOf<Renderable>(
            InfoTextRenderable(CustomWidget.customWidgetParamInfo(), centered = true, padding = 0)
        ).also {
            it.addAll(CustomWidget.widgetStringDataPoints.map { dataPoint ->
                DataPointRenderable(dataPoint)
            })
        }, 6
    )

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        internalWidth = 200
        internalHeight = screenHeight - 100
        SelectiveScreenDrawer.renderPopupBackground(screenDrawing, x, y, width, height, 10, 0.15f)
        renderRenderables(screenDrawing, mouseX, mouseY)
    }

    override fun init() {
        if (width < 20 || height < 20) return
        addRenderable(plane(x + 10, y + 10, width - 20, height - 20))
    }

    class DataPointRenderable(val dataPoint: CustomWidget.WidgetStringData) : Renderable() {
        init {
            internalWidth = 180
            internalHeight = 24
        }

        override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
            screenDrawing.drawText(dataPoint.name().append(" ".toText()).append(("{${dataPoint.id}}").toText().setColor((General.getThemeColor(black = 0.5f)).argb)), x, y, General.getThemeColor())
            val texts = screenDrawing.drawWrappedText(dataPoint.description().string, x, y + 10, width, General.getThemeColor(alpha = 0.7f))
            val paramTexts = dataPoint.param?.let { screenDrawing.drawWrappedText("Param: " + it().string, x, y + 10 + texts.size * 10, width, General.getThemeColor(alpha = 0.4f)) } ?: emptyList()
            internalHeight = 9 + texts.size * 10 + paramTexts.size * 10
        }
    }
}
