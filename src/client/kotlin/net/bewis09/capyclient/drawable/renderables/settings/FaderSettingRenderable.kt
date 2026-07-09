package net.bewis09.capyclient.drawable.renderables.settings

import net.bewis09.capyclient.drawable.renderables.components.button.ResetButton
import net.bewis09.capyclient.drawable.renderables.components.setting.Fader
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.features.sidebar.General
import net.bewis09.capyclient.settings.logic.SettingInterfaceWithDefault
import net.bewis09.capyclient.util.number.Precision

open class FaderSettingRenderable<T : Number>(val title: Translation, val description: Translation?, val setting: SettingInterfaceWithDefault<T>, val precision: Precision, val parser: (original: Float) -> T) : SettingRenderable(description, 22) {
    val fader = Fader(
        value = { setting.get().toFloat() }, onChange = { value ->
            setting.set(parser(value))
        }, precision = precision
    )

    val resetButton = ResetButton(setting) { setting.get() == setting.getDefault() }

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        drawVerticalCenteredText(screenDrawing, title)
        renderRenderables(screenDrawing, mouseX, mouseY)
        screenDrawing.drawRightAlignedText(precision.roundToString(setting.get().toFloat()), x2 - fader.width - 12 - resetButton.width, fontYCenter, General.getTextThemeColor())
    }

    override fun init() {
        super.init()
        addRenderable(resetButton.setPosition(x2 - resetButton.width - 4, y + 4))
        addRenderable(fader.setWidth(if (this.width > 200) 100 else 50).setPosition(x2 - fader.width - 8 - resetButton.width, y + 4))
    }
}