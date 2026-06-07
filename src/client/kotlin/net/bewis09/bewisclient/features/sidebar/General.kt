package net.bewis09.bewisclient.features.sidebar

import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.common.color
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.common.within
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.bewisclient.settings.structure.SidebarFeature
import net.bewis09.bewisclient.drawable.renderables.screen.OptionScreen
import net.bewis09.bewisclient.features.cosmetics.EnableOnlineModeSettingsRenderable
import net.bewis09.bewisclient.features.sidebar.Home.addToQuickSettings
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.types.BooleanSetting
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.util.Bewisclient
import net.bewis09.bewisclient.util.color.StaticColorSaver
import net.bewis09.bewisclient.util.color.ThemeColorSaver

object General : SidebarFeature(createIdentifier("bewisclient", "options_menu"), "Settings") {
    val animationTime = int("animation_time", 200, 0, 500)
    val blurBackground = boolean("blur_background", true)
    val buttonInTitleScreen = boolean("button_in_title_screen", true)
    val buttonInGameScreen = boolean("button_in_game_screen", true)
    val themeColor = color("theme_color", StaticColorSaver(0xFFFFFF.color), ColorSetting.STATIC)
    val backgroundColor = color("background_color", ThemeColorSaver(0.2f), ColorSetting.STATIC, ColorSetting.THEME)
    val backgroundOpacity = float("background_opacity", 0.8f, 0f, 1f, 0.01f, 2)
    val minecraftyOptionsMenu: BooleanSetting = boolean("minecrafty_options_menu", false) { _, _ ->
        if (Bewisclient.getCurrentRenderableScreen()?.renderable !is OptionScreen) return@boolean

        setRenderableScreen(OptionScreen(1f, 1f).apply { changeCategory(General, true) })
    }
    val autoUpdate = boolean("auto_update", System.getProperty("os.name").lowercase().contains("win"))
    val acceptedEULA = boolean("accepted_eula", false) { _, new ->
        if (new == false) onlineMode.set(false)
    }
    val onlineMode: BooleanSetting = boolean("online_mode", false)

    fun getBackgroundColor(): Color = 0.3f within (Color.BLACK to backgroundColor.get().getColor()) alpha backgroundOpacity.get()

    fun getThemeColor(white: Float = 1f, alpha: Float = 1f, black: Float = 1f) = (black within (Color.BLACK to if (isMinecrafty) Color.WHITE else (white within (Color.WHITE to themeColor.get().getColor())))) alpha alpha
    fun getTextThemeColor() = if (!minecraftyOptionsMenu) (0.5f within (Color.WHITE to themeColor.get().getColor())) else Color.WHITE

    override fun getRenderable(): Renderable {
        return VerticalAlignScrollPlane(
            listOfNotNull(
//            OptionsMenuSettings.animationTime.createRenderable("menu.settings.animation_time", "Animation Time", "The time (in milliseconds) it takes for animations to complete"),
                blurBackground.createRenderable("settings.blur_background", "Blur Background", "Whether to blur the background when opening menus").addToQuickSettings("menu.category.settings", "blur"),
                buttonInTitleScreen.createRenderable("settings.button_in_title_screen", "Button in Title Screen", "Whether to show the Bewisclient button in the title screen").addToQuickSettings("menu.category.settings", "title"),
                buttonInGameScreen.createRenderable("settings.button_in_game_screen", "Button in Game Screen", "Whether to show the Bewisclient button in the in-game pause menu").addToQuickSettings("menu.category.settings", "in-game"),
                themeColor.createRenderable("settings.theme_color", "Theme Color", "The theme color used throughout the client").addToQuickSettings("menu.category.settings", "theme_color"),
                backgroundColor.createRenderableWithFader("settings.background_color", "Background Color", "The background color used for menus. Reset to use the theme color.", backgroundOpacity).addToQuickSettings("menu.category.settings", "background"),
                minecraftyOptionsMenu.createRenderable("settings.minecrafty_options_menu", "Minecrafty Options Menu", "Whether to use a Minecrafty style options menu instead of the default flat design"),
                if (System.getProperty("os.name").lowercase().contains("win"))
                    autoUpdate.createRenderable("settings.auto_update", "Automatic Updates", "Whether to automatically check for updates and update the client when an update is found")
                else null,
                EnableOnlineModeSettingsRenderable(
                    Translation("menu.settings.online_mode", "Online Mode"),
                    Translation("menu.settings.online_mode.description", "Whether to enable online features such as special cosmetics and cosmetic syncing. Needs to be enabled if you want other players to see your cosmetics or if you want to see other players' cosmetics. Requires restarting the client to take effect."),
                    onlineMode
                ),
            ), 1
        )
    }
}
