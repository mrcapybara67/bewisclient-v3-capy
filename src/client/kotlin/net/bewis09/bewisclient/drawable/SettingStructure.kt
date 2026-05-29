package net.bewis09.bewisclient.drawable

import net.bewis09.bewisclient.api.APIEntrypointLoader
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.cosmetics.CosmeticType
import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.bewisclient.drawable.renderables.components.button.Button
import net.bewis09.bewisclient.drawable.renderables.components.element.TextElement
import net.bewis09.bewisclient.drawable.renderables.components.structure.Plane
import net.bewis09.bewisclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.bewisclient.drawable.renderables.components.structure.VerticalScrollGrid
import net.bewis09.bewisclient.drawable.renderables.impl.ExtensionListRenderable
import net.bewis09.bewisclient.drawable.renderables.impl.SelectCapeElement
import net.bewis09.bewisclient.drawable.renderables.options_structure.HomePlane
import net.bewis09.bewisclient.drawable.renderables.options_structure.SidebarCategory
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.drawable.renderables.screen.OptionScreen
import net.bewis09.bewisclient.features.cosmetics.CosmeticLoader
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.impl.GeneralSettings
import net.bewis09.bewisclient.settings.structure.Feature
import net.bewis09.bewisclient.util.logic.ClientInterface
import net.bewis09.bewisclient.widget.WidgetLoader

object SettingStructure : ClientInterface {
    val widgetRenderables = WidgetLoader.widgets.map(Feature::createRenderable)

    val utilities = APIEntrypointLoader.mapEntrypoint { it.getUtilities() }.flatten().map(Feature::createRenderable)

    val settings = VerticalAlignScrollPlane(
        listOfNotNull(
//            OptionsMenuSettings.animationTime.createRenderable("menu.settings.animation_time", "Animation Time", "The time (in milliseconds) it takes for animations to complete"),
            GeneralSettings.blurBackground.createRenderable("menu.settings.blur_background", "Blur Background", "Whether to blur the background when opening menus").addToQuickSettings("menu.category.settings", "blur"),
            GeneralSettings.buttonInTitleScreen.createRenderable("menu.settings.button_in_title_screen", "Button in Title Screen", "Whether to show the Bewisclient button in the title screen").addToQuickSettings("menu.category.settings", "title"),
            GeneralSettings.buttonInGameScreen.createRenderable("menu.settings.button_in_game_screen", "Button in Game Screen", "Whether to show the Bewisclient button in the in-game pause menu").addToQuickSettings("menu.category.settings", "in-game"),
            GeneralSettings.themeColor.createRenderable("menu.settings.theme_color", "Theme Color", "The theme color used throughout the client").addToQuickSettings("menu.category.settings", "theme_color"),
            GeneralSettings.backgroundColor.createRenderableWithFader("menu.settings.background_color", "Background Color", "The background color used for menus. Reset to use the theme color.", GeneralSettings.backgroundOpacity).addToQuickSettings("menu.category.settings", "background"),
            GeneralSettings.minecraftyOptionsMenu.createRenderable("menu.settings.minecrafty_options_menu", "Minecrafty Options Menu", "Whether to use a Minecrafty style options menu instead of the default flat design"),
            if (System.getProperty("os.name").lowercase().contains("win"))
                GeneralSettings.autoUpdate.createRenderable("menu.settings.auto_update", "Automatic Updates", "Whether to automatically check for updates and update the client when an update is found")
            else null
        ), 1
    )

    val cosmetics = Plane { x, y, width, height ->
        listOf(
            CosmeticLoader.elytra.createRenderable("cosmetics.elytra", "Apply cape to elytra", "Some capes include a unique texture for the elytra, which can be disabled here if desired.")(x, y, width, 22),
            VerticalScrollGrid({
                CosmeticLoader.allowedCosmetics.map { it to CosmeticLoader.cosmetics[it] }.filter {
                    it.first.type == CosmeticType.CAPE && CosmeticLoader.allowedCosmetics.contains(it.first) && it.second != null
                }.map { a -> SelectCapeElement(a.first, a.second!!) }
            }, 5, 65)(x, y + 27, width, height - 27)
        )
    }

    val extensions = VerticalAlignScrollPlane(APIEntrypointLoader.mapContainer { ExtensionListRenderable(it.provider, it.entrypoint) }, 1)

    val generalWidgetSettings = APIEntrypointLoader.mapEntrypoint { it.getGeneralWidgetSettings() }.flatten()

    val widgetsPlane = Plane { x, y, width, height ->
        listOf(
            Button(Translation("menu.widgets.general_setting", "General Widget Settings")()) {
                OptionScreen.currentInstance?.openPage(
                    TextElement(Translation("menu.widgets.general_setting", "General Widget Settings")(), centered = true).setHeight(12), VerticalAlignScrollPlane({ generalWidgetSettings }, 1)
                )
            }(x, y, width, SelectiveScreenDrawer.getSideButtonHeight()),
//            Button(Translation("menu.widgets.presets", "Presets")()) {
//
//            }(x + width - 55, 37, 55, 14),
            VerticalScrollGrid({ widgetRenderables.map { a -> a.setHeight(90) } }, 5, 80).invoke(x, y + SelectiveScreenDrawer.getSideButtonHeight() + 5, width, height - SelectiveScreenDrawer.getSideButtonHeight() - 5)
        )
    }

    val homeCategory = SidebarCategory(createIdentifier("bewisclient", "home"), "Bewisclient", HomePlane)

    val widgetsCategory = SidebarCategory(createIdentifier("bewisclient", "widgets"), "Widgets", this.widgetsPlane)
    val utilitiesCategory = SidebarCategory(createIdentifier("bewisclient", "utilities"), "Utilities", this.utilities)
    val settingsCategory = SidebarCategory(createIdentifier("bewisclient", "settings"), "Settings", this.settings)
    val cosmeticsCategory = SidebarCategory(createIdentifier("bewisclient", "cosmetics"), "Cosmetics", this.cosmetics)
    val extensionsCategory = SidebarCategory(createIdentifier("bewisclient", "extensions"), "Extensions", this.extensions)
}