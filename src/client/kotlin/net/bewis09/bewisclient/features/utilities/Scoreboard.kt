package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.ImageFeature

object Scoreboard : ImageFeature(createIdentifier("bewisclient", "scoreboard"), "Scoreboard") {
    val scale = float("scale", 1.0f, 0.5f, 2.0f, 0.01f, 2)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(scale, "scoreboard.scale", "Scale", "Adjust the size of the scoreboard", "scale")
    }
}