package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.ImageFeature
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.util.color.StaticColorSaver

object BlockHighlight : ImageFeature(createIdentifier("bewisclient", "block_highlight"), "Block Highlight") {
    val color = color("color", StaticColorSaver(0f, 0f, 0f), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val thickness = float("thickness", 0.4f, 0f, 1f, 0.01f, 2)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, color, "block_highlight.color", "Color", "Change the color of the block highlight", "color")
        list.addRenderable(this, thickness, "block_highlight.thickness", "Thickness", "Adjust the thickness of the block highlight", "thickness")
    }
}