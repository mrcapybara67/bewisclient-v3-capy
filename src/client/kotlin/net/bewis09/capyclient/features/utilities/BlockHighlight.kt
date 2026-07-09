package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature
import net.bewis09.capyclient.settings.types.ColorSetting
import net.bewis09.capyclient.util.color.StaticColorSaver

object BlockHighlight : ImageFeature(createIdentifier("capyclient", "block_highlight"), "Block Highlight") {
    val color = color("color", StaticColorSaver(0f, 0f, 0f), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val thickness = float("thickness", 0.4f, 0f, 1f, 0.01f, 2)
    val filled = boolean("filled", false)
    val filledOpacity = float("filled_opacity", 0.2f, 0f, 1f, 0.01f, 2)
    val range = float("range", 6f, 1f, 32f, 0.5f, 1)
    val onlyWhenSneaking = boolean("only_when_sneaking", false)
    val pulse = boolean("pulse", false)
    val pulseSpeed = float("pulse_speed", 1.0f, 0.1f, 5.0f, 0.1f, 2)
    val lineStyle = string("line_style", "smooth")

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, color, "color", "Color", "Change the color of the block highlight", "color")
        list.addRenderable(this, thickness, "thickness", "Thickness", "Adjust the thickness of the block highlight", "thickness")
        list.addRenderable(this, lineStyle, "line_style",
            "Line Style",
            "smooth / crisp / glow. Controls how the highlight outline is drawn.",
            "line_style"
        )
        list.addRenderable(this, filled, "filled",
            "Filled Box",
            "Also draw a translucent box around the targeted block.",
            "filled"
        )
        list.addRenderable(this, filledOpacity, "filled_opacity",
            "Filled Opacity",
            "Opacity of the filled box when 'Filled Box' is on. 0 = invisible, 1 = fully solid.",
            "filled_opacity"
        )
        list.addRenderable(this, range, "range",
            "Range (blocks)",
            "Only highlight blocks within this many blocks of the player. Vanilla is 6.",
            "range"
        )
        list.addRenderable(this, onlyWhenSneaking, "only_when_sneaking",
            "Only When Sneaking",
            "Only show the highlight when the player is sneaking.",
            "only_when_sneaking"
        )
        list.addRenderable(this, pulse, "pulse",
            "Pulse Animation",
            "Animate the highlight thickness to make it easier to spot the target.",
            "pulse"
        )
        list.addRenderable(this, pulseSpeed, "pulse_speed",
            "Pulse Speed",
            "How fast the highlight pulses. Higher = faster.",
            "pulse_speed"
        )
    }
}
