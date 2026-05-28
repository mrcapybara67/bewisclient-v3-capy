package net.bewis09.bewisclient.settings.impl

import net.bewis09.bewisclient.features.cosmetics.CosmeticLoader
import net.bewis09.bewisclient.features.screenshot.ScreenshotSettings
import net.bewis09.bewisclient.features.utilities.*
import net.bewis09.bewisclient.settings.types.ObjectSetting

object BewisclientSettingsObject : ObjectSetting() {
    init {
        create("options_menu", GeneralSettings)
        create("widgets", WidgetSettings)
        create("fullbright", Fullbright)
        create("block_highlight", BlockHighlight)
        create("zoom", Zoom)
        create("held_item_tooltip", HeldItemTooltip)
        create("pumpkin_overlay", PumpkinOverlay)
        create("better_visibility", BetterVisibility)
        create("scoreboard", Scoreboard)
        create("entity_highlight", EntityHighlight)
        create("perspective", Perspective)
        create("shulker_box_tooltip", ShulkerBoxTooltip)
        create("cosmetics", CosmeticLoader)
        create("screenshot", ScreenshotSettings)
        create("fire_height", FireHeight)
        create("pack_adder", PackAdder)
        create("panorama", Panorama)
        create("home", HomePlaneSettings)
    }
}
