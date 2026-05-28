package net.bewis09.bewisclient.drawable.renderables.components

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Translations
import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.settings.impl.GeneralSettings
import net.bewis09.bewisclient.util.interfaces.Settable
import net.bewis09.bewisclient.version.setCursorPointer

class ResetButton<T>(val setting: Settable<T?>) : TooltipHoverable(Translations.RESET()) {
    init {
        internalWidth = 14
        internalHeight = 14
    }

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)

        if (isMouseOver(mouseX.toDouble(), mouseY.toDouble()))
            screenDrawing.setCursorPointer()

        screenDrawing.pushColor(0.8f, 0.8f, 0.8f, 1f)
        SelectiveScreenDrawer.renderButtonBackground(screenDrawing, hoverFactor, 0f, x, y, width, height, 1f, false, mouseX, mouseY)
        screenDrawing.popColor()

        val imagePadding = if (isMinecrafty) 3 else 2
        screenDrawing.drawTexture(createIdentifier("bewisclient", "textures/gui/sprites/reset.png"), x + imagePadding, y + imagePadding, width - imagePadding * 2, height - imagePadding * 2, GeneralSettings.getTextThemeColor())
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean = setting.set(null).let { true }
}