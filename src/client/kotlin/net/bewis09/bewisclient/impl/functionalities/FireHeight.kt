package net.bewis09.bewisclient.impl.functionalities

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.game.Translation
import net.bewis09.bewisclient.settings.structure.ImageFeature

object FireHeight: ImageFeature("fire_height", Translation("menu.category.fire_height", "Fire Height")) {
    val height = float("height", 1f, 0f, 1f, 0.01f, 2)

    override val settingRenderables: Array<Renderable> = arrayOf(
        height.createRenderable(
            "fire_height.height", "Fire Height", "Adjust the height of the fire overlay on your screen"
        ).addToQuickSettings("menu.category.fire_height", "height"),
    )
}