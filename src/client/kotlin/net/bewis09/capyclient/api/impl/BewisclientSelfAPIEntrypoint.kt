// @VersionReplacement

package net.bewis09.capyclient.api.impl

import net.bewis09.capyclient.api.BewisclientAPIEntrypoint
import net.bewis09.capyclient.common.EntityTypes
import net.bewis09.capyclient.common.id
import net.bewis09.capyclient.drawable.ImageIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.SidebarFeature
import net.bewis09.capyclient.features.sidebar.Contact
import net.bewis09.capyclient.features.cosmetics.CosmeticLoader
import net.bewis09.capyclient.features.sidebar.Extensions
import net.bewis09.capyclient.features.sidebar.Screenshot
import net.bewis09.capyclient.features.sidebar.Utilities
import net.bewis09.capyclient.features.utilities.AutoGG
import net.bewis09.capyclient.features.utilities.BetterVisibility
import net.bewis09.capyclient.features.utilities.BlockHighlight
import net.bewis09.capyclient.features.utilities.EntityHighlight
import net.bewis09.capyclient.features.utilities.FeatureIcons
import net.bewis09.capyclient.features.utilities.FireHeight
import net.bewis09.capyclient.features.utilities.FlatItems
import net.bewis09.capyclient.features.utilities.FoodSaturationOverlay
import net.bewis09.capyclient.features.utilities.Fullbright
import net.bewis09.capyclient.features.utilities.HeldItemTooltip
import net.bewis09.capyclient.features.utilities.ItemPhysics
import net.bewis09.capyclient.features.utilities.NoChatLag
import net.bewis09.capyclient.features.utilities.PackAdder
import net.bewis09.capyclient.features.utilities.Panorama
import net.bewis09.capyclient.features.utilities.Perspective
import net.bewis09.capyclient.features.utilities.PlayerNametag
import net.bewis09.capyclient.features.utilities.PumpkinOverlay
import net.bewis09.capyclient.features.utilities.Scoreboard
import net.bewis09.capyclient.features.utilities.ShulkerBoxTooltip
import net.bewis09.capyclient.features.utilities.TntTimer
import net.bewis09.capyclient.features.utilities.TntTimer.FuseProvider
import net.bewis09.capyclient.features.utilities.Zoom
import net.bewis09.capyclient.game.BewisclientCommand
import net.bewis09.capyclient.game.BewisclientResourcePack
import net.bewis09.capyclient.game.ShulkerBoxTooltipComponent
import net.bewis09.capyclient.game.Ticker
import net.bewis09.capyclient.game.keybinds.Keybind
import net.bewis09.capyclient.game.keybinds.KeybindingImplementer
import net.bewis09.capyclient.game.keybinds.OpenOptionScreen
import net.bewis09.capyclient.game.translations.TranslationLoader
import net.bewis09.capyclient.features.sidebar.General
import net.bewis09.capyclient.features.sidebar.Home
import net.bewis09.capyclient.features.sidebar.Widgets
import net.bewis09.capyclient.settings.logic.Settings
import net.bewis09.capyclient.settings.structure.CategorizedFeature
import net.bewis09.capyclient.settings.structure.Feature
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.widget.Widget
import net.bewis09.capyclient.widget.WidgetLoader
import net.bewis09.capyclient.widget.impl.ArmorWidget
import net.bewis09.capyclient.widget.impl.BiomeWidget
import net.bewis09.capyclient.widget.impl.CPSWidget
import net.bewis09.capyclient.widget.impl.CoordinatesWidget
import net.bewis09.capyclient.widget.impl.CustomWidget
import net.bewis09.capyclient.widget.impl.DayWidget
import net.bewis09.capyclient.widget.impl.DaytimeWidget
import net.bewis09.capyclient.widget.impl.FPSWidget
import net.bewis09.capyclient.widget.impl.InventoryWidget
import net.bewis09.capyclient.widget.impl.KeyWidget
import net.bewis09.capyclient.widget.impl.PingWidget
import net.bewis09.capyclient.widget.impl.ServerWidget
import net.bewis09.capyclient.widget.impl.SpeedWidget
import net.bewis09.capyclient.widget.impl.TiwylaWidget
import kotlin.jvm.optionals.getOrNull

class BewisclientSelfAPIEntrypoint : BewisclientAPIEntrypoint() {
    override fun getEventEntrypoints(): List<EventEntrypoint> = listOf(
        WidgetLoader, Settings, KeybindingImplementer, TranslationLoader, BiomeWidget, SpeedWidget, TiwylaWidget, ShulkerBoxTooltipComponent.Entrypoint, CosmeticLoader, BewisclientCommand, ImageIdentifier, Panorama, Ticker, AutoGG, FeatureIcons
    )

    override fun getKeybinds(): List<Keybind> = listOf(
        OpenOptionScreen, Fullbright.ToggleNightVision, Fullbright.ToggleFullbright, Fullbright.IncreaseBrightness, Fullbright.DecreaseBrightness, Zoom.ZoomKeybind, Perspective.EnablePerspective, Panorama.TakePanoramaScreenshot, PlayerNametag.ToggleNametag
    )

    override fun getWidgets(): List<Widget> = listOf(
        FPSWidget, BiomeWidget, DayWidget, CoordinatesWidget, DaytimeWidget, PingWidget, CPSWidget, KeyWidget, InventoryWidget, SpeedWidget, TiwylaWidget, CustomWidget, ServerWidget, ArmorWidget
    )

    override fun getUtilities(): List<CategorizedFeature> = listOf(
        Fullbright, BlockHighlight, EntityHighlight, HeldItemTooltip, Zoom, PumpkinOverlay, BetterVisibility, Scoreboard, ShulkerBoxTooltip, Perspective, FireHeight, Panorama, TntTimer, PackAdder, AutoGG, PlayerNametag, NoChatLag, FlatItems, ItemPhysics, FoodSaturationOverlay
    )

    override fun getOtherSettings(): List<Feature> {
        return listOf(Home)
    }

    override fun getGeneralWidgetSettings(): List<Renderable> = listOf(
        Widgets.Default.gap.createRenderable(General, "gap", "Gap", "Set the gap between widgets in a row"),
        Widgets.Default.screenEdgeDistance.createRenderable(General, "screen_edge_distance", "Screen Edge Distance", "Set the snapping distance of a widget to the screen edge"),
        Widgets.Default.backgroundColor.createRenderableWithFader(General, "default_background", "Default Background", "Set the default color and opacity of a widget", Widgets.Default.backgroundOpacity),
        Widgets.Default.borderColor.createRenderableWithFader(General, "default_border", "Default Border", "Set the default color and opacity of a widget's border", Widgets.Default.borderOpacity),
        Widgets.Default.paddingSize.createRenderable(General, "default_padding_size", "Default Padding Size", "Set the default padding at the edge of a widget to the text"),
        Widgets.Default.lineSpacing.createRenderable(General, "default_line_spacing", "Default Line Spacing", "Set the default spacing between lines of text in a widget"),
        Widgets.Default.shadow.createRenderable(General, "default_text_shadow", "Default Text Shadow", "Set whether text in a widget has a shadow by default"),
        Widgets.Default.textColor.createRenderable(General, "default_text_color", "Default Text Color", "Set the default color of the text in a widget"),
        Widgets.Default.borderRadius.createRenderable(General, "default_border_radius", "Default Border Radius", "Set the default radius of a widget's border corners"),
        Widgets.Default.scale.createRenderable(General, "default_scale", "Default Scale", "Set the default scale of a widget"),
    )

    override fun getSidebarCategories(): List<SidebarFeature> = listOf(
        Widgets, Utilities, General, CosmeticLoader, Extensions, Screenshot, Contact
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

    override fun getTntTimerEntities() = listOf(
        FuseProvider(EntityTypes.TNT, true) { entity -> entity.fuse },
        FuseProvider(EntityTypes.TNT_MINECART, false) { entity -> entity.fuse },
        // @[26.1] @[] FuseProvider(EntityTypes.SULFUR_CUBE, false) { entity -> entity.fuse }
        /*[@]*//*[!@]*/
    )
}