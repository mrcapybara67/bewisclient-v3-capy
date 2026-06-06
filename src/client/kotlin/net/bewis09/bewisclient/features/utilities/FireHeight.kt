package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.ImageFeature

object FireHeight : ImageFeature(createIdentifier("bewisclient", "fire_height"), "Fire Height") {
    val height = float("height", 1f, 0f, 1f, 0.01f, 2)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(height, "fire_height.height", "Fire Height", "Adjust the height of the fire overlay on your screen", "height")
    }
}