package net.bewis09.bewisclient.drawable.renderables.settings

import net.bewis09.bewisclient.drawable.renderables.components.button.ResetButton
import net.bewis09.bewisclient.drawable.renderables.components.setting.Input
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.types.Setting

class StringSettingRenderable(
    val title: Translation,
    val description: Translation?,
    val setting: Setting<String>
) : SettingRenderable(description, 22) {
    val input = Input(
        text = setting.get(),
        onChange = setting::set,
    )

    val resetButton = ResetButton(setting, setting::isDefault)

    init {
        // Keep the Input field visually in sync with the underlying setting
        // (ResetButton writes setting.set(null) which calls this listener with
        // newValue=null, after which setting.get() returns the default).
        setting.onChangeListener = { _, _ ->
            val current = setting.get()
            if (input.text != current) input.setText(current)
        }
    }

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        drawVerticalCenteredText(screenDrawing, title)
        renderRenderables(screenDrawing, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        val inputWidth = (this.width - resetButton.width - 16).coerceAtLeast(40)
        addRenderable(input.setPosition(x + 8, y + 5).setSize(inputWidth, 12))
        addRenderable(resetButton.setPosition(x2 - resetButton.width - 4, y + 4))
    }
}
