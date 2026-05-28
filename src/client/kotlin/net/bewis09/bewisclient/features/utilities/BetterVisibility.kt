// @VersionReplacement

package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.structure.ImageFeature
import net.bewis09.bewisclient.settings.types.BooleanSetting

object BetterVisibility : ImageFeature("better_visibility", Translation("menu.category.better_visibility", "Better Visibility")) {
    val nether = boolean("nether", false)
    val water = boolean("water", false)
    val lava = boolean("lava", false)
    val powder_snow = boolean("powder_snow", false)

    class FogModifierConfig(val setting: BooleanSetting, val start: (Float) -> Float, val end: (Float) -> Float)

    val fogModifiers = mapOf(
        "atmospheric" to FogModifierConfig(nether, { it * 2 - (it / 10.0f).coerceIn(4.0f, 64.0f) }, { it * 2 }),
        "water" to FogModifierConfig(water, { -8f }, { it }),
        "lava" to FogModifierConfig(lava, { -8f }, { 16f }),
        "powder_snow" to FogModifierConfig(powder_snow, { -8f }, { 8f })
    )

    override val settingRenderables: Array<Renderable> = arrayOf(
        nether.createRenderable("better_visibility.nether", "Nether", "Improve visibility in the Nether dimension").addToQuickSettings("menu.category.better_visibility", "nether"),
        water.createRenderable("better_visibility.water", "Water", "Enhance visibility underwater").addToQuickSettings("menu.category.better_visibility", "water"),
        lava.createRenderable("better_visibility.lava", "Lava", "Boost visibility in lava").addToQuickSettings("menu.category.better_visibility", "lava"),
        powder_snow.createRenderable("better_visibility.powder_snow", "Powder Snow", "Increase visibility in powder snow").addToQuickSettings("menu.category.better_visibility", "snow")
    )

    fun applyFogModifier(instance: String, fogData: FogData, viewDistance: Float) {
        if (isEnabled()) fogModifiers[instance]?.let {
            if (!it.setting.get()) return

            val start = it.start(viewDistance)
            val end = it.end(viewDistance)

            if (end > fogData.environmentalEnd) {
                fogData.environmentalEnd = end
                fogData.environmentalStart = start
            }
        }
    }
}

// @[1.21.5] class FogData(var environmentalStart: Float, var environmentalEnd: Float) @[] typealias FogData = net.minecraft.client.renderer.fog.FogData
/*[@]*/typealias FogData = net.minecraft.client.renderer.fog.FogData/*[!@]*/