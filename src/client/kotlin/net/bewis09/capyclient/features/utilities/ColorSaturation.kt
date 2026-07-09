package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.common.setColor
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.game.keybinds.Keybind
import net.bewis09.capyclient.settings.structure.ImageFeature
import org.lwjgl.glfw.GLFW

/**
 * Color Saturation module — AppleSkin-style toggle.
 *
 * Press the configured keybind (default: unbound — use the settings menu
 * to assign it) to instantly toggle colour saturation on/off.  When on,
 * the saturation is boosted to a fixed multiplier (1.40x by default) for
 * a vibrant, contrast-rich look.
 *
 * - Toggle ON  → saturation = 1.40 (punchy/contrast-y colours)
 * - Toggle OFF → saturation = 1.00 (vanilla)
 *
 * The actual rendering is handled by [ColorSaturationMixin], which
 * post-processes the light texture pixels.
 */
object ColorSaturation : ImageFeature(createIdentifier("capyclient", "color_saturation"), "Color Saturation") {
    /**
     * The saturation multiplier applied when the feature is toggled ON.
     * 1.0 = vanilla, 1.4 = AppleSkin-like punch, 2.0 = max.
     */
    val saturationStrength = float("saturation_strength", 1.4f, 1.0f, 2.0f, 0.05f, 1)

    val toggledOnTranslation = createTranslation("toggled_on", "Color Saturation ON")
    val toggledOffTranslation = createTranslation("toggled_off", "Color Saturation OFF")

    /**
     * Keybind to toggle Color Saturation on/off.
     * Default: unbound (GLFW_KEY_UNKNOWN = -1).  The user assigns a key
     * via Minecraft's vanilla Controls screen → Capy Client category.
     */
    object ToggleColorSaturation : Keybind(GLFW.GLFW_KEY_UNKNOWN, "color_saturation.toggle", "Toggle Color Saturation", {
        enabled.set(!enabled.get())
        if (enabled.get()) {
            showTitle(toggledOnTranslation().setColor(0x55FF55))
        } else {
            showTitle(toggledOffTranslation().setColor(0xFF5555))
        }
    })

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, saturationStrength, "saturation_strength",
            "Saturation Strength",
            "Colour boost when active. 1.0 = vanilla, 1.4 = vibrant/contrasty (AppleSkin-style), 2.0 = max.",
            "saturation_strength"
        )
    }
}
