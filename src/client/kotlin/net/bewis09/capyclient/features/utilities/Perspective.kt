package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.game.keybinds.Keybind
import net.bewis09.capyclient.settings.structure.ImageFeature
import org.lwjgl.glfw.GLFW

object Perspective : ImageFeature(createIdentifier("capyclient", "perspective"), "Perspective") {
    @JvmField
    var cameraAddPitch: Float = 0f

    @JvmField
    var cameraAddYaw: Float = 0f

    /**
     * Toggle state for the "hold_to_toggle = false" mode. When the user wants
     * a click-on / click-off key (the classic Minecraft 1.13+ Perspective mod
     * behaviour) this flips on every key press. The mixin reads it once per
     * frame and zeroes [cameraAddPitch]/[cameraAddYaw] when transitioning
     * from active to inactive so the camera snaps back to vanilla.
     *
     * @Volatile because the keybind action runs on the main client thread
     * but the mixin also reads it from a @HEAD injection on Entity.turn
     * (also main thread, but Kotlin requires the explicit annotation for
     * cross-class @JvmField access in case of future threading changes).
     */
    @Volatile
    @JvmField
    var perspectiveActive: Boolean = false

    val sensitivity = float("sensitivity", 0.15f, 0.01f, 1f, 0.01f, 2)
    val holdToToggle = boolean("hold_to_toggle", true)
    val restoreOnDisable = boolean("restore_on_disable", true)
    val maxPitch = float("max_pitch", 90f, 0f, 90f, 1f, 0)
    val maxYaw = float("max_yaw", 180f, 0f, 360f, 1f, 0)

    object EnablePerspective : Keybind(
        GLFW.GLFW_KEY_LEFT_ALT,
        "perspective.enable_perspective",
        "Perspective",
        action = {
            // The keybind's `tick` callback already runs every frame with the
            // current down-state, so we don't need to do anything here in
            // "hold" mode (the mixin reads `isPressed()` directly). Only flip
            // the toggle flag in toggle mode.
            if (!Perspective.holdToToggle.get()) {
                perspectiveActive = !perspectiveActive
            }
        }
    )

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, sensitivity, "sensitivity",
            "Sensitivity",
            "How fast the camera rotates while the perspective key is held. Lower values give finer aim.",
            "sensitivity"
        )
        list.addRenderable(this, holdToToggle, "hold_to_toggle",
            "Hold to Toggle",
            "When on, perspective only activates while the key is held. When off, the key toggles perspective on/off.",
            "hold_to_toggle"
        )
        list.addRenderable(this, restoreOnDisable, "restore_on_disable",
            "Restore on Disable",
            "Snap the camera back to its original rotation when perspective is released/disabled.",
            "restore_on_disable"
        )
        list.addRenderable(this, maxPitch, "max_pitch",
            "Max Pitch (deg)",
            "Maximum pitch the perspective camera can rotate to (clamped).",
            "max_pitch"
        )
        list.addRenderable(this, maxYaw, "max_yaw",
            "Max Yaw (deg)",
            "Maximum yaw the perspective camera can rotate to (clamped).",
            "max_yaw"
        )
    }
}
