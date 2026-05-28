package net.bewis09.bewisclient.settings.structure

import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.impl.GeneralSettings

abstract class ImageFeature(val image: String, text: Translation) : Feature(text) {
    val identifier = createIdentifier("bewisclient", "textures/gui/functionalities/$image.png")

    override fun createRenderable(): SettingCategory = object : SettingCategory() {
        override fun renderContent(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
            val textHeight = (screenDrawing.wrapText(title.getTranslatedString(), width - 10).size - 1) * screenDrawing.getTextHeight()
            screenDrawing.drawCenteredWrappedText(title.getTranslatedString(), centerX, y2 - 27 - textHeight / 3 * 2, width - 10, if (isMinecrafty) Color.WHITE else GeneralSettings.getThemeColor(white = state.get() / 2))
            screenDrawing.drawTexture(identifier, centerX - 20, y + 14, 40, 40, if (isMinecrafty) Color.WHITE else GeneralSettings.getThemeColor(white = state.get()))
        }
    }
}