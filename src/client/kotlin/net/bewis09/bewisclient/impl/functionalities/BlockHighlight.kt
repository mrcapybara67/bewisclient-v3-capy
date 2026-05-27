package net.bewis09.bewisclient.impl.functionalities

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.game.Translation
import net.bewis09.bewisclient.settings.structure.ImageFeature
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.util.color.StaticColorSaver

object BlockHighlight : ImageFeature("block_highlight", Translation("menu.category.block_highlight", "Block Highlight")) {
    val color = color("color", StaticColorSaver(0f, 0f, 0f), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val thickness = float("thickness", 0.4f, 0f, 1f, 0.01f, 2)

    override val settingRenderables: Array<Renderable> = arrayOf(
        color.createRenderable("block_highlight.color", "Color", "Change the color of the block highlight").addToQuickSettings("menu.category.block_highlight", "color"),
        thickness.createRenderable("block_highlight.thickness", "Thickness", "Adjust the thickness of the block highlight").addToQuickSettings("menu.category.block_highlight", "thickness"),
    )
}