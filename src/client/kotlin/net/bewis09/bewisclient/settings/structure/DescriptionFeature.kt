package net.bewis09.bewisclient.settings.structure

import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.common.Identifier
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.impl.GeneralSettings

abstract class DescriptionFeature(id: Identifier, title: String, descriptionText: String) : CategorizedFeature(id, title) {
    val description = Translation(id.namespace, "menu.category.${id.path}.description", descriptionText)

    override fun createRenderable() = object : SettingCategory() {
        override fun renderContent(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
            val textHeight = (screenDrawing.wrapText(title.getTranslatedString(), width - 10).size - 1) * screenDrawing.getTextHeight()
            screenDrawing.drawCenteredWrappedText(title.getTranslatedString(), centerX, y + 14 - textHeight / 2, width - 10, if (isMinecrafty) Color.WHITE else GeneralSettings.getThemeColor(state.get() / 2))
            val descriptionHeight = (screenDrawing.wrapText(description.getTranslatedString(), width - 10).size - 1) * screenDrawing.getTextHeight()
            screenDrawing.drawCenteredWrappedText(description.getTranslatedString(), centerX, y2 - 42 - descriptionHeight / 2, width - 10, if (isMinecrafty) Color.WHITE alpha 0.65f else GeneralSettings.getThemeColor(state.get() / 2, 0.65f))
        }
    }
}