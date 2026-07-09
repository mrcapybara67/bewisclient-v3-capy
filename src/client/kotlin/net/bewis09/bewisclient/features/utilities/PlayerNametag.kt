package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.common.color
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.game.keybinds.Keybind
import net.bewis09.bewisclient.settings.structure.ImageFeature
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.util.color.StaticColorSaver
import org.lwjgl.glfw.GLFW

object PlayerNametag : ImageFeature(createIdentifier("bewisclient", "player_nametag"), "Player Nametag") {
    val rainbow = boolean("rainbow", false)
    val color = color("color", StaticColorSaver(0xFFFFFF.color), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val rainbowSpeed = float("rainbow_speed", 1f, 0.05f, 10f, 0.05f, 2)
    val visibleDistance = int("visible_distance", 64, 4, 256)

    val selfNametag = boolean("self_nametag", true)
    val selfNametagMode = string("self_mode", "third_person")
    val showInTab = boolean("show_in_tab", true)
    val showInScoreboard = boolean("show_in_scoreboard", true)
    val scale = float("scale", 1.0f, 0.5f, 2.0f, 0.05f, 2)
    val prefix = string("prefix", "")
    val suffix = string("suffix", "")
    val bold = boolean("bold", false)
    val italic = boolean("italic", false)
    val backgroundColor = color("background_color", StaticColorSaver(0x000000.color), ColorSetting.STATIC, ColorSetting.CHANGING)
    val backgroundOpacity = float("background_opacity", 0f, 0f, 1f, 0.01f, 2)
    val showHealth = boolean("show_health", false)
    val showPing = boolean("show_ping", false)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, rainbow, "rainbow", "Rainbow Color", "Animates the nametag color through the hue spectrum over time.", "rainbow")
        list.addRenderable(this, rainbowSpeed, "rainbow_speed", "Rainbow Speed", "Hue rotation speed in cycles-per-second-like units. Higher = faster change.", "rainbow_speed")
        list.addRenderable(this, color, "color", "Static Color", "Color used when 'Rainbow Color' is disabled.", "color")
        list.addRenderable(this, visibleDistance, "visible_distance", "Visible Distance (blocks)", "Don't render custom nametags for players farther than this in blocks.", "visible_distance")
        list.addRenderable(this, scale, "scale",
            "Scale",
            "Scale of the rendered nametag (1.0 = vanilla).",
            "scale"
        )
        list.addRenderable(this, selfNametag, "self_nametag",
            "Show Own Nametag",
            "When on, your own player nametag is also rendered with the chosen color in third-person (F5) view.",
            "self_nametag"
        )
        list.addRenderable(this, selfNametagMode, "self_mode",
            "Own Nametag Mode",
            "always / third_person / never. Always: show in any camera mode. Third-person: only in F5. Never: never show your own tag.",
            "self_mode"
        )
        list.addRenderable(this, showInTab, "show_in_tab",
            "Show in Tab List",
            "Apply the custom color/format to the player's name in the Tab list.",
            "show_in_tab"
        )
        list.addRenderable(this, showInScoreboard, "show_in_scoreboard",
            "Show in Scoreboard",
            "Apply the custom color/format to the player's name in the scoreboard.",
            "show_in_scoreboard"
        )
        list.addRenderable(this, prefix, "prefix",
            "Prefix",
            "Text prepended to every player's name (e.g. team tag).",
            "prefix"
        )
        list.addRenderable(this, suffix, "suffix",
            "Suffix",
            "Text appended to every player's name.",
            "suffix"
        )
        list.addRenderable(this, bold, "bold",
            "Bold",
            "Render the name in bold.",
            "bold"
        )
        list.addRenderable(this, italic, "italic",
            "Italic",
            "Render the name in italic.",
            "italic"
        )
        list.addRenderable(this, backgroundColor, "background_color",
            "Background Color",
            "Optional background quad behind each nametag. Set background opacity > 0 to enable.",
            "background_color"
        )
        list.addRenderable(this, backgroundOpacity, "background_opacity",
            "Background Opacity",
            "Background opacity. 0 = no background, 1 = fully opaque quad behind the name.",
            "background_opacity"
        )
        list.addRenderable(this, showHealth, "show_health",
            "Show Health",
            "Show a tiny health bar under the nametag.",
            "show_health"
        )
        list.addRenderable(this, showPing, "show_ping",
            "Show Ping",
            "Show the player's ping (ms) next to the nametag.",
            "show_ping"
        )
    }

    object ToggleNametag : Keybind(GLFW.GLFW_KEY_F7, "player_nametag.toggle", "Toggle Player Nametag", {
        enabled.set(!enabled.get())
    })
}
