package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

/**
 * Food Saturation Overlay — AppleSkin-style overlay on the hunger bar.
 *
 * Shows the player's current saturation level as a semi-transparent
 * golden overlay on the hunger bar, and when holding a food item,
 * displays the hunger that would be restored as additional translucent
 * shanks.
 *
 * The overlay is rendered by [FoodSaturationOverlayMixin] which hooks
 * into the HUD render pipeline. This is a separate feature from
 * [ColorSaturation] (the screen color-vibrancy filter).
 */
object FoodSaturationOverlay : ImageFeature(createIdentifier("capyclient", "food_saturation_overlay"), "Food Saturation Overlay") {

    /**
     * Whether to show the saturation overlay on the hunger bar.
     */
    val showSaturationBar = boolean("show_saturation_bar", true)

    /**
     * Whether to show a preview of food restoration when holding food.
     */
    val showFoodPreview = boolean("show_food_preview", true)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, showSaturationBar, "show_saturation_bar",
            "Show Saturation Bar",
            "Display your current saturation level as a golden overlay on the hunger bar.",
            "show_saturation_bar"
        )
        list.addRenderable(
            this, showFoodPreview, "show_food_preview",
            "Show Food Preview",
            "Display translucent hunger shanks when holding a food item, showing how much hunger would be restored.",
            "show_food_preview"
        )
    }
}
