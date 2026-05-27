// @VersionReplacement

package net.bewis09.bewisclient.impl

import net.bewis09.bewisclient.impl.functionalities.BetterVisibility
import net.bewis09.bewisclient.settings.types.BooleanSetting

object BetterVisibilityImpl {
    class FogModifierConfig(val setting: BooleanSetting, val start: (Float) -> Float, val end: (Float) -> Float)

    val fogModifiers = mapOf(
        "atmospheric" to FogModifierConfig(BetterVisibility.nether, { it * 2 - (it / 10.0f).coerceIn(4.0f, 64.0f) }, { it * 2 }),
        "water" to FogModifierConfig(BetterVisibility.water, { -8f }, { it }),
        "lava" to FogModifierConfig(BetterVisibility.lava, { -8f }, { 16f }),
        "powder_snow" to FogModifierConfig(BetterVisibility.powder_snow, { -8f }, { 8f })
    )

    fun applyFogModifier(instance: String, fogData: FogData, viewDistance: Float) {
        if (BetterVisibility.isEnabled()) fogModifiers[instance]?.let {
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