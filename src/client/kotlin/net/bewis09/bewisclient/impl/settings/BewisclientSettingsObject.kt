package net.bewis09.bewisclient.impl.settings

import net.bewis09.bewisclient.cosmetics.CosmeticLoader
import net.bewis09.bewisclient.impl.functionalities.BetterVisibility
import net.bewis09.bewisclient.impl.functionalities.BlockHighlight
import net.bewis09.bewisclient.impl.functionalities.EntityHighlight
import net.bewis09.bewisclient.impl.functionalities.FireHeight
import net.bewis09.bewisclient.impl.functionalities.Fullbright
import net.bewis09.bewisclient.impl.functionalities.HeldItemTooltip
import net.bewis09.bewisclient.impl.functionalities.PackAdder
import net.bewis09.bewisclient.impl.functionalities.Panorama
import net.bewis09.bewisclient.impl.functionalities.Perspective
import net.bewis09.bewisclient.impl.functionalities.PumpkinOverlay
import net.bewis09.bewisclient.impl.functionalities.Scoreboard
import net.bewis09.bewisclient.impl.functionalities.ShulkerBoxTooltip
import net.bewis09.bewisclient.impl.functionalities.Zoom
import net.bewis09.bewisclient.impl.screenshot.ScreenshotSettings
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
