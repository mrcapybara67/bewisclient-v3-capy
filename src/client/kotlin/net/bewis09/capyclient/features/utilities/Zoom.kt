package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Animator
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.game.keybinds.Keybind
import net.bewis09.capyclient.settings.structure.ImageFeature
import org.lwjgl.glfw.GLFW

object Zoom : ImageFeature(createIdentifier("capyclient", "zoom"), "Zoom") {
    val smooth = boolean("smooth", true)
    val instant = boolean("instant", false)
    val factor = float("factor", 0.23f, 0.05f, 0.95f, 0.01f, 2)
    val minFactor = float("min_factor", 0.05f, 0.01f, 0.5f, 0.01f, 2)
    val smoothDuration = int("smooth_duration_ms", 100, 0, 500)
    val scrollSensitivity = float("scroll_sensitivity", 0.02f, 0.0f, 0.1f, 0.005f, 3)
    val scrollEnabled = boolean("scroll_to_zoom", true)
    val resetOnRelease = boolean("reset_on_release", true)
    val keepSmoothCamera = boolean("keep_smooth_camera", true)

    var smoothCameraEnabledBefore: Boolean? = null

    val ZoomKeybind = Keybind(GLFW.GLFW_KEY_C, "zoom.use", "Zoom", null) {
        setUsed(it)
    }

    var factorAnimation = Animator({ (if (instant.get()) 1 else smoothDuration.get()).toLong() }, Animator.EASE_OUT, 1f)

    override val enabledByDefault: Boolean
        get() = true

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, smooth, "smooth", "Smooth Zoom", "Enable or disable smooth zoom (Works as if smooth camera is enabled)", "smooth")
        list.addRenderable(this, instant, "instant", "Instant Zoom", "Disables the transition animation when zooming in or out", "instant")
        list.addRenderable(this, factor, "factor",
            "Zoom Factor",
            "How much to zoom in (lower = more zoom). 0.23 = vanilla-accurate, 0.05 = extreme close-up.",
            "factor"
        )
        list.addRenderable(this, minFactor, "min_factor",
            "Minimum Zoom Factor",
            "Lower bound when using the scroll wheel to zoom in further.",
            "min_factor"
        )
        list.addRenderable(this, smoothDuration, "smooth_duration",
            "Smooth Duration (ms)",
            "Duration in milliseconds of the zoom-in/out animation. 0 = instant.",
            "smooth_duration"
        )
        list.addRenderable(this, scrollEnabled, "scroll_to_zoom",
            "Scroll Wheel Zoom",
            "Allow the scroll wheel to fine-tune the zoom level while zoom is active.",
            "scroll_to_zoom"
        )
        list.addRenderable(this, scrollSensitivity, "scroll_sensitivity",
            "Scroll Sensitivity",
            "How much the scroll wheel changes the zoom factor. Higher = more aggressive.",
            "scroll_sensitivity"
        )
        list.addRenderable(this, resetOnRelease, "reset_on_release",
            "Reset on Release",
            "Snap the zoom back to 1.0 when the key is released.",
            "reset_on_release"
        )
        list.addRenderable(this, keepSmoothCamera, "keep_smooth_camera",
            "Keep Smooth Camera",
            "Temporarily enable vanilla smooth-camera while zoomed, restore prior setting on release.",
            "keep_smooth_camera"
        )
    }

    fun getFactor(): Float {
        return if (enabled.get()) factorAnimation.get() else 1f
    }

    fun isUsed(): Boolean {
        return factorAnimation.getWithoutInterpolation() != 1f
    }

    fun setUsed(used: Boolean) {
        if (!isUsed() && used) {
            smoothCameraEnabledBefore = client.options.smoothCamera
            if (smooth.get() && keepSmoothCamera.get()) {
                client.options.smoothCamera = true
            }
            factorAnimation.set(factor.get())
        } else if (!used && isUsed()) {
            if (smooth.get() && keepSmoothCamera.get()) {
                client.options.smoothCamera = smoothCameraEnabledBefore ?: false
            }
            if (resetOnRelease.get()) factorAnimation.set(1f)
        }
    }
}
