package net.bewis09.bewisclient.api.impl

import net.bewis09.bewisclient.api.BewisclientAPIEntrypoint
import net.bewis09.bewisclient.common.EntityTypes
import net.bewis09.bewisclient.common.id
import net.bewis09.bewisclient.drawable.ImageIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.SidebarCategory
import net.bewis09.bewisclient.features.contact.Contact
import net.bewis09.bewisclient.features.cosmetics.CosmeticLoader
import net.bewis09.bewisclient.features.screenshot.Screenshot
import net.bewis09.bewisclient.features.utilities.*
import net.bewis09.bewisclient.game.BewisclientCommand
import net.bewis09.bewisclient.game.BewisclientResourcePack
import net.bewis09.bewisclient.game.ShulkerBoxTooltipComponent
import net.bewis09.bewisclient.game.Ticker
import net.bewis09.bewisclient.game.keybinds.Keybind
import net.bewis09.bewisclient.game.keybinds.KeybindingImplementer
import net.bewis09.bewisclient.game.keybinds.OpenOptionScreen
import net.bewis09.bewisclient.game.translations.TranslationLoader
import net.bewis09.bewisclient.server.Authorization
import net.bewis09.bewisclient.server.AutoUpdater
import net.bewis09.bewisclient.server.Security
import net.bewis09.bewisclient.settings.impl.BewisclientSettings
import net.bewis09.bewisclient.settings.impl.DefaultWidgetSettings
import net.bewis09.bewisclient.settings.logic.Settings
import net.bewis09.bewisclient.settings.logic.SettingsLoader
import net.bewis09.bewisclient.settings.structure.Feature
import net.bewis09.bewisclient.util.EventEntrypoint
import net.bewis09.bewisclient.widget.Widget
import net.bewis09.bewisclient.widget.WidgetLoader
import net.bewis09.bewisclient.widget.impl.*
import kotlin.jvm.optionals.getOrNull

class BewisclientSelfAPIEntrypoint : BewisclientAPIEntrypoint() {
    override fun getEventEntrypoints(): List<EventEntrypoint> = listOf(
        WidgetLoader, SettingsLoader, KeybindingImplementer, TranslationLoader, BiomeWidget, SpeedWidget, TiwylaWidget, ShulkerBoxTooltipComponent.Entrypoint, CosmeticLoader, BewisclientCommand, Security, ImageIdentifier, Panorama, Ticker, AutoUpdater, Authorization
    )

    override fun getSettingsObjects(): List<Settings> = listOf(
        BewisclientSettings
    )

    override fun getKeybinds(): List<Keybind> = listOf(
        OpenOptionScreen, Fullbright.ToggleNightVision, Fullbright.ToggleFullbright, Fullbright.IncreaseBrightness, Fullbright.DecreaseBrightness, Zoom.ZoomKeybind, Perspective.EnablePerspective, Panorama.TakePanoramaScreenshot
    )

    override fun getWidgets(): List<Widget> = listOf(
        FPSWidget, BiomeWidget, DayWidget, CoordinatesWidget, DaytimeWidget, PingWidget, CPSWidget, KeyWidget, InventoryWidget, SpeedWidget, TiwylaWidget, CustomWidget, ServerWidget, ArmorWidget
    )

    override fun getUtilities(): List<Feature> = listOf(
        Fullbright, BlockHighlight, EntityHighlight, HeldItemTooltip, Zoom, PumpkinOverlay,
        // Crosshair,
        BetterVisibility, Scoreboard, ShulkerBoxTooltip, Perspective, FireHeight, Panorama, PackAdder
        // Chat Enhancements,
    )

    override fun getGeneralWidgetSettings(): List<Renderable> = listOf(
        DefaultWidgetSettings.gap.createRenderable("widget.gap", "Gap", "Set the gap between widgets in a row"),
        DefaultWidgetSettings.screenEdgeDistance.createRenderable("widget.screen_edge_distance", "Screen Edge Distance", "Set the snapping distance of a widget to the screen edge"),
        DefaultWidgetSettings.backgroundColor.createRenderableWithFader("widget.default_background", "Default Background", "Set the default color and opacity of a widget", DefaultWidgetSettings.backgroundOpacity),
        DefaultWidgetSettings.borderColor.createRenderableWithFader("widget.default_border", "Default Border", "Set the default color and opacity of a widget's border", DefaultWidgetSettings.borderOpacity),
        DefaultWidgetSettings.paddingSize.createRenderable("widget.default_padding_size", "Default Padding Size", "Set the default padding at the edge of a widget to the text"),
        DefaultWidgetSettings.lineSpacing.createRenderable("widget.default_line_spacing", "Default Line Spacing", "Set the default spacing between lines of text in a widget"),
        DefaultWidgetSettings.shadow.createRenderable("widget.default_text_shadow", "Default Text Shadow", "Set whether text in a widget has a shadow by default"),
        DefaultWidgetSettings.textColor.createRenderable("widget.default_text_color", "Default Text Color", "Set the default color of the text in a widget"),
        DefaultWidgetSettings.borderRadius.createRenderable("widget.default_border_radius", "Default Border Radius", "Set the default radius of a widget's border corners"),
        DefaultWidgetSettings.scale.createRenderable("widget.default_scale", "Default Scale", "Set the default scale of a widget"),
    )

    override fun getSidebarCategories(): List<SidebarCategory> = listOf(
        Screenshot, Contact
    )

    override fun getTiwylaEntityExtraInfoProviders(): List<TiwylaWidget.EntityInfoProvider<*>> = listOf(
        TiwylaWidget.EntityInfoProvider(EntityTypes.CAT) { it.variant.unwrapKey().getOrNull()?.id()?.toString() },
        TiwylaWidget.EntityInfoProvider(EntityTypes.FROG) { it.variant.unwrapKey().getOrNull()?.id()?.toString() },
        TiwylaWidget.EntityInfoProvider(EntityTypes.AXOLOTL) { it.variant.name },
        TiwylaWidget.EntityInfoProvider(EntityTypes.HORSE) { it.markings.name.lowercase() + ", " + it.variant.name.lowercase() },
        TiwylaWidget.EntityInfoProvider(EntityTypes.RABBIT) { it.variant.name.lowercase() },
        TiwylaWidget.EntityInfoProvider(EntityTypes.LLAMA) { it.variant.name.lowercase() },
    )

    override fun getCustomResourceProviders(): List<BewisclientResourcePack.CustomResourceProvider> = listOf(
        Panorama
    )
}