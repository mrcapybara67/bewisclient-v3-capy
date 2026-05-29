package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.drawable.Animator
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.game.keybinds.Keybind
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.structure.ImageFeature
import org.lwjgl.glfw.GLFW

object Zoom : ImageFeature("zoom", Translation("menu.category.zoom", "Zoom")) {
    val smooth = boolean("smooth", true)
    val instant = boolean("instant", false)

    var smoothCameraEnabledBefore: Boolean? = null

    val ZoomKeybind = Keybind(GLFW.GLFW_KEY_C, "zoom.use", "Zoom", null) {
        setUsed(it)
    }

    var factorAnimation = Animator({ if (instant.get()) 1 else 100 }, Animator.EASE_OUT, 1f)

    override val enabledByDefault: Boolean
        get() = true

    override val settingRenderables: Array<Renderable> = arrayOf(
        smooth.createRenderable("zoom.smooth", "Smooth Zoom", "Enable or disable smooth zoom (Works as if smooth camera is enabled)").addToQuickSettings("menu.category.zoom", "smooth"),
        instant.createRenderable("zoom.instant", "Instant Zoom", "Disables the transition animation when zooming in or out").addToQuickSettings("menu.category.zoom", "instant")
    )

    fun getFactor(): Float {
        return if (enabled.get()) factorAnimation.get() else 1f
    }

    fun isUsed(): Boolean {
        return factorAnimation.getWithoutInterpolation() != 1f
    }

    fun setUsed(used: Boolean) {
        if (!isUsed() && used) {
            smoothCameraEnabledBefore = client.options.smoothCamera
            if (smooth.get()) {
                client.options.smoothCamera = true
            }
            factorAnimation.set(0.23f)
        } else if (!used && isUsed()) {
            if (smooth.get()) {
                client.options.smoothCamera = smoothCameraEnabledBefore ?: false
            }
            factorAnimation.set(1f)
        }
    }
}