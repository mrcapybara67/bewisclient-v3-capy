package net.bewis09.capyclient.drawable.renderables.settings

import net.bewis09.capyclient.drawable.renderables.components.button.ResetButton
import net.bewis09.capyclient.drawable.renderables.components.setting.Switch
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.settings.types.Setting

class BooleanSettingRenderable(val title: Translation, val description: Translation?, val setting: Setting<Boolean>) : SettingRenderable(description, 22) {
    val switch = Switch(
        state = setting::get,
        onChange = setting::set,
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
        addRenderable(switch.setPosition(x2 - switch.width - 8 - resetButton.width, y + 5))
    }
}