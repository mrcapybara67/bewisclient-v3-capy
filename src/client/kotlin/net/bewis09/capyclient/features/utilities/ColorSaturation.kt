package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.common.setColor
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.game.keybinds.Keybind
import net.bewis09.capyclient.settings.structure.ImageFeature
import org.lwjgl.glfw.GLFW

/**
 * Food Saturation module — AppleSkin-style overlay.
 *
 * Shows the player's current saturation level as a semi-transparent
 * overlay on the hunger bar, and when holding a food item, displays
 * the hunger and saturation that would be restored as additional
 * translucent shanks.
 *
 * - Saturation Overlay: A golden translucent bar behind the regular
 *   hunger bar showing how much saturation the player has.
 * - Preview Overlay: When holding food, shows translucent shanks
 *   representing how much hunger would be restored by eating it.
 *
 * The overlay is rendered by [FoodSaturationOverlayMixin] which
 * hooks into the HUD render pipeline.
 */
object ColorSaturation : ImageFeature(createIdentifier("capyclient", "color_saturation"), "Food Saturation") {
    /**
     * Whether to show the saturation overlay on the hunger bar.
     */
    val showSaturationBar = boolean("show_saturation_bar", true)

    /**
     * Whether to show a preview of food restoration when holding food.
     */
    val showFoodPreview = boolean("show_food_preview", true)

    val toggledOnTranslation = createTranslation("toggled_on", "Food Saturation ON")
    val toggledOffTranslation = createTranslation("toggled_off", "Food Saturation OFF")

    /**
     * Keybind to toggle Food Saturation on/off.
     */
    object ToggleColorSaturation : Keybind(GLFW.GLFW_KEY_UNKNOWN, "color_saturation.toggle", "Toggle Food Saturation", {
        enabled.set(!enabled.get())
        if (enabled.get()) {
            showTitle(toggledOnTranslation().setColor(0x55FF55))
        } else {
            showTitle(toggledOffTranslation().setColor(0xFF5555))
        }
    })

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
