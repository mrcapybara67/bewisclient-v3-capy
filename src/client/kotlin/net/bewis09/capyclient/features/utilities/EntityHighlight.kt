package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.color
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature
import net.bewis09.capyclient.settings.types.ColorSetting
import net.bewis09.capyclient.util.color.StaticColorSaver

object EntityHighlight : ImageFeature(createIdentifier("capyclient", "entity_highlight"), "Entity Highlight") {
    val color = color("color", StaticColorSaver(0xFF0000.color), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val alpha = float("alpha", 0.31f, 0f, 1f, 0.01f, 2)
    val range = float("range", 24f, 1f, 128f, 1f, 0)
    val onlyHostile = boolean("only_hostile", false)
    val onlyPassive = boolean("only_passive", false)
    val onlyPlayers = boolean("only_players", false)
    val onlyWhenLooking = boolean("only_when_looking", false)
    val showOwnEntity = boolean("show_self", false)
    val friendlyColor = color("friendly_color", StaticColorSaver(0x55FF55.color), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val showHealth = boolean("show_health", false)
    // NOTE: a `pulse` / `pulseSpeed` setting pair was prototyped but cannot be
    // wired to EntityHighlightMixin because the mixin targets OverlayTexture.<init>
    // (one-shot texture bake, not per-frame). Surfacing the toggle would just
    // confuse users. If/when a per-frame mixin is added, re-introduce the pair.

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, color, "color", "Color", "Change the color of the entity highlight", "color")
        list.addRenderable(this, alpha, "alpha", "Transparency", "Adjust the transparency of the entity highlight", "alpha")
        list.addRenderable(this, friendlyColor, "friendly_color",
            "Friendly Color",
            "Color used for friendly mobs/players (when the related filter is on).",
            "friendly_color"
        )
        list.addRenderable(this, range, "range",
            "Range (blocks)",
            "Only highlight entities within this many blocks of the player.",
            "range"
        )
        list.addRenderable(this, onlyHostile, "only_hostile",
            "Only Hostile Mobs",
            "Restrict the highlight to hostile mobs only.",
            "only_hostile"
        )
        list.addRenderable(this, onlyPassive, "only_passive",
            "Only Passive Mobs",
            "Restrict the highlight to passive mobs only.",
            "only_passive"
        )
        list.addRenderable(this, onlyPlayers, "only_players",
            "Only Players",
            "Restrict the highlight to players only.",
            "only_players"
        )
        list.addRenderable(this, showOwnEntity, "show_self",
            "Highlight Self",
            "Also highlight your own player entity (in F5, etc.).",
            "show_self"
        )
        list.addRenderable(this, onlyWhenLooking, "only_when_looking",
            "Only When Looking",
            "Only highlight entities the crosshair is currently aimed at.",
            "only_when_looking"
        )
        list.addRenderable(this, showHealth, "show_health",
            "Show Health Bar",
            "Draw a small health indicator under the highlighted entity.",
            "show_health"
        )
    }
}
