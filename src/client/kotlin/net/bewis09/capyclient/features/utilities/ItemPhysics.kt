package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

/**
 * Item Physics module — makes dropped items fall with gravity, spin
 * while falling, and come to rest lying flat on the ground instead of
 * hovering and spinning like vanilla.
 */
object ItemPhysics : ImageFeature(createIdentifier("capyclient", "item_physics"), "Item Physics") {
    /**
     * How fast items spin while falling. 0 disables mid-air rotation,
     * higher values make items tumble faster.
     */
    val rotationSpeed = float("rotation_speed", 1.0f, 0.0f, 5.0f, 0.1f, 1)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, rotationSpeed, "rotation_speed",
            "Rotation Speed",
            "How fast items spin while falling to the ground. Set to 0 to disable rotation.",
            "rotation_speed"
        )
    }
}
