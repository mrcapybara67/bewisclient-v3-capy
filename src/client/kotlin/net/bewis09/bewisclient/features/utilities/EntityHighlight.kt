package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.common.color
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.structure.ImageFeature
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.util.color.StaticColorSaver

object EntityHighlight : ImageFeature("entity_highlight", Translation("menu.category.entity_highlight", "Entity Highlight")) {
    val color = color("color", StaticColorSaver(0xFF0000.color), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
    val alpha = float("alpha", 0.31f, 0f, 1f, 0.01f, 2)

    override val settingRenderables: Array<Renderable> = arrayOf(
        color.createRenderable("entity_highlight.color", "Color", "Change the color of the entity highlight").addToQuickSettings("menu.category.entity_highlight", "color"),
        alpha.createRenderable("entity_highlight.alpha", "Transparency", "Adjust the transparency of the entity highlight").addToQuickSettings("menu.category.entity_highlight", "alpha"),
    )
}