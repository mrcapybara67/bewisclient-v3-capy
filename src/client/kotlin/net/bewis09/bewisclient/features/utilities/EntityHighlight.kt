package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.common.color
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.ImageFeature
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.util.color.StaticColorSaver

object EntityHighlight : ImageFeature(createIdentifier("bewisclient","entity_highlight"), "Entity Highlight") {
    val color = color("color", StaticColorSaver(0xFF0000.color), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val alpha = float("alpha", 0.31f, 0f, 1f, 0.01f, 2)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, color, "color", "Color", "Change the color of the entity highlight", "color")
        list.addRenderable(this, alpha, "alpha", "Transparency", "Adjust the transparency of the entity highlight", "alpha")
    }
}