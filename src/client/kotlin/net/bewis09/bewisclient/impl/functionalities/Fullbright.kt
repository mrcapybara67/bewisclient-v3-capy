package net.bewis09.bewisclient.impl.functionalities

import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.drawable.renderables.settings.InfoTextRenderable
import net.bewis09.bewisclient.game.Keybind
import net.bewis09.bewisclient.game.Translation
import net.bewis09.bewisclient.impl.settings.GeneralSettings
import net.bewis09.bewisclient.common.color
import net.bewis09.bewisclient.common.within
import net.bewis09.bewisclient.common.setColor
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.ImageFeature
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import org.lwjgl.glfw.GLFW

object Fullbright : ImageFeature("fullbright", Translation("menu.category.fullbright", "Fullbright")) {
    val nightVision = boolean("night_vision", false)
    val brightness = float("brightness", 1f, 0f, 15f, 0.01f, 2)

    val nightVisionEnabledTranslation = Translation("fullbright.night_vision.enabled", "Night Vision Enabled")
    val nightVisionDisabledTranslation = Translation("fullbright.night_vision.disabled", "Night Vision Disabled")

    val brightnessTranslation = Translation("fullbright.brightness", "Brightness: %s")

    object ToggleNightVision : Keybind(GLFW.GLFW_KEY_H, "fullbright.toggle_night_vision", "Toggle Night Vision", {
        nightVision.toggle()
        if (hasNightVision()) {
            showTitle(nightVisionEnabledTranslation().setColor(0xFFFF55))
        } else {
            showTitle(nightVisionDisabledTranslation().setColor(0xFF5555))
        }
    })

    object ToggleFullbright : Keybind(GLFW.GLFW_KEY_G, "fullbright.toggle_fullbright", "Toggle Fullbright", {
        brightness.set(if (brightness.get() > 1f) 1f else 15f)

        enabled.set(true)

        showFullbrightMessage()
    })

    object IncreaseBrightness : Keybind(GLFW.GLFW_KEY_UP, "fullbright.increase_brightness", "Increase Brightness", {
        val current = brightness.get()
        brightness.set(15f.coerceAtMost(current + 0.25f))
        showFullbrightMessage()
    })

    object DecreaseBrightness : Keybind(GLFW.GLFW_KEY_DOWN, "fullbright.decrease_brightness", "Decrease Brightness", {
        val current = brightness.get()
        brightness.set(0f.coerceAtLeast(current - 0.25f))
        showFullbrightMessage()
    })

    override val settingRenderables: Array<Renderable> = arrayOf(
        brightness.createRenderable(
            "fullbright.brightness", "Brightness", "Adjust the brightness level. 0.0 to 1.0 are the normal levels, while 1.0 to 15.0 is lighting up the world according to the brightness level"
        ).addToQuickSettings("menu.category.fullbright", "brightness"),
        nightVision.createRenderable("fullbright.night_vision", "Night Vision", "Allows you to have the visual effect of night vision without actually having it").addToQuickSettings("menu.category.fullbright", "night_vision"),
        InfoTextRenderable(
            Translation(
                "fullbright.night_vision.error_text", "When night vision is applied via Bewisclient, the effect will not be the same as if you got it via a potion, because Bewisclient preserves the old way in which night vision works, which illuminates the world completely, whilst with the status effect it is always rendered as if the brightness is set all the way down to moody."
            )(), 0xAAAAAA.color * GeneralSettings.getThemeColor(), true
        ),
    )

    fun showFullbrightMessage() {
        val value = brightness.get()
        showTitle(brightnessTranslation((value * 100).toString() + "%").setColor(((value / 15) within (0xFF0000.color to 0xFFFF00.color)).argb))
    }

    private val nightVisionInstance = MobEffectInstance(MobEffects.NIGHT_VISION, -1, 255, false, false, false)

    fun getNightVisionInstance(): MobEffectInstance? {
        return if (hasNightVision()) nightVisionInstance else null
    }

    fun hasNightVision(): Boolean {
        return nightVision.get() && enabled.get()
    }
}