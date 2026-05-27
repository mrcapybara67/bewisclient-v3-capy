package net.bewis09.bewisclient.settings.structure

import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.game.Translation
import net.bewis09.bewisclient.impl.settings.GeneralSettings

abstract class DescriptionFeature(text: Translation, val description: Translation) : Feature(text) {
    override fun createRenderable() = object : SettingCategory() {
        override fun renderContent(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
            val textHeight = (screenDrawing.wrapText(title.getTranslatedString(), width - 10).size - 1) * screenDrawing.getTextHeight()
            screenDrawing.drawCenteredWrappedText(title.getTranslatedString(), centerX, y + 14 - textHeight / 2, width - 10, if(isMinecrafty) Color.WHITE else GeneralSettings.getThemeColor(state.get() / 2))
            val descriptionHeight = (screenDrawing.wrapText(description.getTranslatedString(), width - 10).size - 1) * screenDrawing.getTextHeight()
            screenDrawing.drawCenteredWrappedText(description.getTranslatedString(), centerX, y2 - 42 - descriptionHeight / 2, width - 10, if(isMinecrafty) Color.WHITE alpha 0.65f else GeneralSettings.getThemeColor(state.get() / 2, 0.65f))
        }
    }
}