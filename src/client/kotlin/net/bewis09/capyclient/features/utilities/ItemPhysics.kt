package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

/**
 * Item Physics module — makes dropped items fall realistically and
 * lie flat on the ground instead of floating/spinning in mid-air.
 *
 * Vanilla Minecraft items spin on the spot while they are on the ground.
 * This module overrides the rotation and velocity of item entities so
 * they:
 * - Fall with realistic gravity-like wobble
 * - Lie flat on the ground (rotation set to 0 on the relevant axes)
 * - Stop bouncing once they touch the ground
 *
 * The physics overrides are applied by [ItemPhysicsMixin].
 */
object ItemPhysics : ImageFeature(createIdentifier("capyclient", "item_physics"), "Item Physics") {
    /** Whether items should lie flat (prone) on the ground. */
    val layFlat = boolean("lay_flat", true)

    /**
     * Enable realistic "wobble" when items are falling — the item tilts
     * slightly as it descends rather than falling perfectly straight.
     */
    val wobble = boolean("wobble", true)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, layFlat, "lay_flat",
            "Lay Flat on Ground",
            "Dropped items rotate to lie flat on the ground surface " +
                "instead of standing upright and spinning.",
            "lay_flat"
        )
        list.addRenderable(
            this, wobble, "wobble",
            "Realistic Fall Wobble",
            "Items wobble/tilt slightly as they fall, mimicking real-world " +
                "physics rather than falling perfectly straight.",
            "wobble"
        )
    }
}
