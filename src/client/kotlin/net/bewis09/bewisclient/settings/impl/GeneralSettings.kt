package net.bewis09.bewisclient.settings.impl

import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.common.color
import net.bewis09.bewisclient.common.within
import net.bewis09.bewisclient.drawable.SettingStructure
import net.bewis09.bewisclient.drawable.renderables.screen.OptionScreen
import net.bewis09.bewisclient.settings.types.BooleanSetting
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.settings.types.ObjectSetting
import net.bewis09.bewisclient.util.Bewisclient
import net.bewis09.bewisclient.util.color.StaticColorSaver
import net.bewis09.bewisclient.util.color.ThemeColorSaver

object GeneralSettings : ObjectSetting() {
    val animationTime = int("animation_time", 200, 0, 500)
    val blurBackground = boolean("blur_background", true)
    val buttonInTitleScreen = boolean("button_in_title_screen", true)
    val buttonInGameScreen = boolean("button_in_game_screen", true)
    val themeColor = color("theme_color", StaticColorSaver(0xFFFFFF.color), ColorSetting.STATIC)
    val backgroundColor = color("background_color", ThemeColorSaver(0.2f), ColorSetting.STATIC, ColorSetting.THEME)
    val backgroundOpacity = float("background_opacity", 0.8f, 0f, 1f, 0.01f, 2)
    val minecraftyOptionsMenu: BooleanSetting = boolean("minecrafty_options_menu", false) { _, _ ->
        if (Bewisclient.getCurrentRenderableScreen()?.renderable !is OptionScreen) return@boolean

        setRenderableScreen(OptionScreen(1f, 1f).apply { changeCategory(SettingStructure.settingsCategory, true) })
    }
    val autoUpdate = boolean("auto_update", System.getProperty("os.name").lowercase().contains("win"))

    fun getBackgroundColor(): Color = 0.3f within (Color.BLACK to backgroundColor.get().getColor()) alpha backgroundOpacity.get()

    fun getThemeColor(white: Float = 1f, alpha: Float = 1f, black: Float = 1f) = (black within (Color.BLACK to if (isMinecrafty) Color.WHITE else (white within (Color.WHITE to themeColor.get().getColor())))) alpha alpha
    fun getTextThemeColor() = if (!minecraftyOptionsMenu) (0.5f within (Color.WHITE to themeColor.get().getColor())) else Color.WHITE
}
