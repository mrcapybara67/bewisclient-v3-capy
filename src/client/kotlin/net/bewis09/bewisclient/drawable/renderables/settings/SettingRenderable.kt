package net.bewis09.bewisclient.drawable.renderables.settings

import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.bewisclient.drawable.renderables.TooltipHoverable
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.drawable.screen_drawing.pushColor
import net.bewis09.bewisclient.drawable.screen_drawing.translate
import net.bewis09.bewisclient.game.Translation
import net.bewis09.bewisclient.impl.settings.OptionsMenuSettings

abstract class SettingRenderable(tooltip: () -> Translation?, height: Int) : TooltipHoverable({ tooltip()?.invoke() }, minHeight = height) {
    constructor(tooltip: Translation? = null, height: Int) : this({ tooltip }, height)

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        screenDrawing.pushColor(0.7f, 0.7f, 0.7f, 1f) {
            SelectiveScreenDrawer.renderButtonBackground(screenDrawing, hoverAnimation.get(), 0f, x, y, width, height, 1f, false, mouseX, mouseY)
        }
    }

    fun drawVerticalCenteredText(screenDrawing: ScreenDrawing, title: Translation) {
        screenDrawing.translate(0f, height / 2f - screenDrawing.getTextHeight() / 2f + 0.5f) {
            screenDrawing.drawText(title(), x + 8, y, OptionsMenuSettings.getTextThemeColor())
        }
    }
}