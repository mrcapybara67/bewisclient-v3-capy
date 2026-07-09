package net.bewis09.capyclient.features.cosmetics

import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.renderables.components.button.Button
import net.bewis09.capyclient.drawable.renderables.components.button.ResetButton
import net.bewis09.capyclient.drawable.renderables.components.setting.Switch
import net.bewis09.capyclient.drawable.renderables.settings.SettingRenderable
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.server.Authorization
import net.bewis09.capyclient.features.sidebar.General
import net.bewis09.capyclient.settings.types.Setting

class EnableOnlineModeSettingsRenderable(val title: Translation, val description: Translation?, val setting: Setting<Boolean>) : SettingRenderable(description, 26 + SelectiveScreenDrawer.getSideButtonHeight()) {
    val switch = Switch(
        state = setting::get,
        onChange = {
            if (!it || General.acceptedEULA())
                setting.set(it)
            else
                AcceptPrivacyPage.openPrivacyPage()
        },
    )

    companion object {
        val reloadWarning = Translation("menu.cosmetics.online_mode_reload_warning", "⚠ You need to restart the game for this setting to take effect.")
        val readPrivacyNotice = Translation("menu.cosmetics.read_privacy_notice", "Privacy Notice")
        val needToAccept = Translation("menu.cosmetics.need_to_accept_privacy_notice", "You need to accept the privacy notice to enable online mode.")
    }

    val resetButton = ResetButton(setting, setting::isDefault)

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        internalHeight = 44
        super.render(screenDrawing, mouseX, mouseY)
        screenDrawing.drawText(title(), x + 8, y + 11 - screenDrawing.getTextHeight() / 2f + if(isMinecrafty) 0f else 0.5f, General.getTextThemeColor())
        if (Authorization.onlineModeEnabled != setting.get())
            screenDrawing.drawText(reloadWarning(), x + 8, y + 22 - screenDrawing.getTextHeight() / 2f + if(isMinecrafty) 0f else 0.5f, General.getTextThemeColor() alpha 0.7f)
        screenDrawing.drawText(needToAccept(), x + 8, y + 33 - screenDrawing.getTextHeight() / 2f + if(isMinecrafty) 0f else 0.5f, General.getTextThemeColor() alpha 0.7f)
        renderRenderables(screenDrawing, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        addRenderable(resetButton.setPosition(x2 - resetButton.width - 4, y + 4))
        addRenderable(switch.setPosition(x2 - switch.width - 8 - resetButton.width, y + 5))
        addRenderable(Button(readPrivacyNotice()) {
            AcceptPrivacyPage.openPrivacyPage()
        }(x2 - 104, y + height - SelectiveScreenDrawer.getSideButtonHeight() * 2 + 14, 100, SelectiveScreenDrawer.getSideButtonHeight()))
    }
}