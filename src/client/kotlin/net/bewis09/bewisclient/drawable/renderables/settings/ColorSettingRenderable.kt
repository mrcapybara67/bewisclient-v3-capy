package net.bewis09.bewisclient.drawable.renderables.settings

import net.bewis09.bewisclient.drawable.renderables.components.button.ColorInfoButton
import net.bewis09.bewisclient.drawable.renderables.components.button.ResetButton
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.types.Setting
import net.bewis09.bewisclient.util.color.ColorSaver

class ColorSettingRenderable(val title: Translation, val description: Translation?, val setting: Setting<ColorSaver>, val types: Array<String>) : SettingRenderable(description, 22) {
    val colorInfoButton = ColorInfoButton(
        state = setting::get, onChange = setting::set, types = types
    )

    val resetButton = ResetButton(setting, setting::isDefault)

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        drawVerticalCenteredText(screenDrawing, title)
        renderRenderables(screenDrawing, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        addRenderable(resetButton.setPosition(x2 - resetButton.width - 4, y + 4))
        addRenderable(colorInfoButton.setPosition(x2 - colorInfoButton.width - 8 - resetButton.width, y + 4))
    }
}