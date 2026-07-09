package net.bewis09.capyclient.drawable.renderables.components.button

import net.bewis09.capyclient.common.Color
import net.bewis09.capyclient.drawable.renderables.components.logic.TooltipHoverable
import net.bewis09.capyclient.drawable.renderables.popup.ColorChangePopup
import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.util.color.ColorSaver
import net.bewis09.capyclient.util.interfaces.Gettable

class ColorInfoButton(val state: Gettable<ColorSaver>, val onChange: (ColorSaver) -> Unit, val types: Array<String>) : TooltipHoverable(changeColorTranslation(), 160, 14) {
    companion object {
        val changeColorTranslation = Translation("menu.color.change_color", "Change Color")
    }

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        val colorSaver = state.get()
        usePointer(screenDrawing, mouseX, mouseY)
        screenDrawing.fillWithBorderRounded(x, y, width, height, if (isMinecrafty) 0 else 5, colorSaver.getColor() alpha hoverFactor * 0.3f + 0.3f, colorSaver.getColor() alpha hoverFactor * 0.5f + 0.5f)
        screenDrawing.drawCenteredText(colorSaver.toInfoString(), exactCenterX, fontYCenter + 0.5f, Color.WHITE)
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean {
        OptionScreen.currentInstance?.openPopup(ColorChangePopup(state, onChange, types))
        return true
    }
}