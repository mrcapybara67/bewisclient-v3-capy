package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

/**
 * Item Physics module — makes dropped items fall realistically and
 * lie flat on the ground instead of floating/spinning in mid-air.
 *
 * **Requires [FlatItems] (2D Items) to be enabled.** This module
 * builds on top of the 2D/Flat rendering to add realistic physics
 * behaviour such as fall wobble and natural ground orientation.
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

    /**
     * Returns true only when both ItemPhysics AND FlatItems are enabled.
     * This ensures Item Physics cannot function without 2D Items active.
     * The settings UI uses [enabled.get()] for the toggle display, while
     * mixins use this method for runtime checks — so if FlatItems is
     * disabled, ItemPhysics silently pauses until FlatItems is re-enabled.
     */
    override fun isEnabled(): Boolean {
        return super.isEnabled() && FlatItems.isEnabled()
    }

    override fun enabledListener(oldValue: Boolean?, newValue: Boolean?) {
        if (newValue == true && !FlatItems.isEnabled()) {
            // Auto-enable FlatItems (2D Items) when Item Physics is turned on
            FlatItems.enabled.set(true)
        }
    }

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, layFlat, "lay_flat",
            "Lay Flat on Ground",
            "Dropped items rotate to lie flat on the ground surface " +
                "instead of standing upright and spinning.\n" +
                "⚠ Requires 2D Items to be enabled.",
            "lay_flat"
        )
        list.addRenderable(
            this, wobble, "wobble",
            "Realistic Fall Wobble",
            "Items wobble/tilt slightly as they fall, mimicking real-world " +
                "physics rather than falling perfectly straight.\n" +
                "⚠ Requires 2D Items to be enabled.",
            "wobble"
        )
    }
}
