package net.bewis09.bewisclient.drawable.renderables

import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.drawable.screen_drawing.translate
import net.bewis09.bewisclient.impl.settings.OptionsMenuSettings
import net.bewis09.bewisclient.version.setCursorPointer
import net.minecraft.network.chat.Component

class Button(var text: Component, val onClick: (Button) -> Unit, tooltip: Component? = null, val selected: (() -> Boolean)? = null, var dark: Boolean = false, val small: Boolean = false) : TooltipHoverable(tooltip) {
    constructor(text: Component, onClick: (Button) -> Unit) : this(text, onClick, null)

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        if (small) {
            SelectiveScreenDrawer.renderSmallButtonBackground(screenDrawing, hoverAnimation.get(), 0f, x, y, width, height, 1f, selected?.invoke() == true, mouseX, mouseY, dark)
        } else {
            SelectiveScreenDrawer.renderButtonBackground(screenDrawing, hoverAnimation.get(), 0f, x, y, width, height, 1f, selected?.invoke() == true, mouseX, mouseY, dark)
        }

        if (isMouseOver(mouseX.toDouble(), mouseY.toDouble()))
            screenDrawing.setCursorPointer()

        screenDrawing.translate(0f, height / 2f - screenDrawing.getTextHeight() / 2f) {
            screenDrawing.drawCenteredText(text, centerX, y, OptionsMenuSettings.getTextThemeColor())
        }
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean = onClick(this).let { true }
}