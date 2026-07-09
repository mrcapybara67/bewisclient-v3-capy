package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

object FireHeight : ImageFeature(createIdentifier("capyclient", "fire_height"), "Fire Height") {
    val height = float("height", 1f, 0f, 1f, 0.01f, 2)
    val opacity = float("opacity", 1f, 0f, 1f, 0.01f, 2)
    val verticalOffset = float("vertical_offset", 0f, -1f, 1f, 0.01f, 2)
    val renderMode = string("render_mode", "translate")

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, height, "height",
            "Fire Height",
            "Adjust the height of the fire overlay on your screen",
            "height"
        )
        list.addRenderable(this, opacity, "opacity",
            "Opacity",
            "How opaque the fire overlay is. 0 = invisible, 1 = full vanilla strength.",
            "opacity"
        )
        list.addRenderable(this, verticalOffset, "vertical_offset",
            "Vertical Offset",
            "Push the fire overlay up (negative) or down (positive) on the screen.",
            "vertical_offset"
        )
        list.addRenderable(this, renderMode, "render_mode",
            "Render Mode",
            "translate / scale / static. Controls how the height setting is applied to the overlay."
        )
    }
}
