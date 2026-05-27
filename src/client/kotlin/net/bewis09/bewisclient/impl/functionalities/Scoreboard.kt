package net.bewis09.bewisclient.impl.functionalities

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.game.Translation
import net.bewis09.bewisclient.settings.structure.ImageFeature

object Scoreboard : ImageFeature("scoreboard", Translation("menu.category.scoreboard", "Scoreboard")) {
    val scale = float("scale", 1.0f, 0.5f, 2.0f, 0.01f, 2)

    override val settingRenderables: Array<Renderable> = arrayOf(
        scale.createRenderable("scoreboard.scale", "Scale", "Adjust the size of the scoreboard").addToQuickSettings("menu.category.scoreboard", "scale")
    )
}