// @VersionReplacement

package net.bewis09.bewisclient.impl

import net.bewis09.bewisclient.impl.settings.functionalities.BetterVisibilitySettings
import net.bewis09.bewisclient.settings.types.BooleanSetting
import net.bewis09.bewisclient.util.MathHelper

// @[1.21.5] @[] import net.minecraft.client.renderer.fog.environment.*
/*[@]*/import net.minecraft.client.renderer.fog.environment.*/*[!@]*/

object BetterVisibilityImpl {
    class FogModifierConfig(val setting: BooleanSetting, val clazz: FogType, val start: (Float) -> Float, val end: (Float) -> Float)

    val fogModifiers = listOf(
        // @[1.21.5] "atmospheric" @[1.21.10] DimensionOrBossFogEnvironment::class.java @[] AtmosphericFogEnvironment::class.java
        FogModifierConfig(BetterVisibilitySettings.nether, /*[@]*/AtmosphericFogEnvironment::class.java/*[!@]*/, { it * 2 - MathHelper.clamp(it / 10.0f, 4.0f, 64.0f) }, { it * 2 }),
        // @[1.21.5] "water" @[] WaterFogEnvironment::class.java
        FogModifierConfig(BetterVisibilitySettings.water, /*[@]*/WaterFogEnvironment::class.java/*[!@]*/, { -8f }, { it }),
        // @[1.21.5] "lava" @[] LavaFogEnvironment::class.java
        FogModifierConfig(BetterVisibilitySettings.lava, /*[@]*/LavaFogEnvironment::class.java/*[!@]*/, { -8f }, { 16f }),
        // @[1.21.5] "powder_snow" @[] PowderedSnowFogEnvironment::class.java
        FogModifierConfig(BetterVisibilitySettings.powder_snow, /*[@]*/PowderedSnowFogEnvironment::class.java/*[!@]*/, { -8f }, { 8f })
    )

    fun applyFogModifier(instance: FogType, fogData: FogData, viewDistance: Float) {
        if (BetterVisibilitySettings.isEnabled()) fogModifiers.find { instance == it.clazz && it.setting.get() }?.let {
            val start = it.start(viewDistance)
            val end = it.end(viewDistance)

            if (end > fogData.environmentalEnd) {
                fogData.environmentalEnd = end
                fogData.environmentalStart = start
            }
        }
    }
}

// @[1.21.5] String @[] Class<out FogEnvironment>
typealias FogType = /*[@]*/Class<out FogEnvironment>/*[!@]*/

// @[1.21.5] class FogData(var environmentalStart: Float, var environmentalEnd: Float) @[] typealias FogData = net.minecraft.client.renderer.fog.FogData
/*[@]*/typealias FogData = net.minecraft.client.renderer.fog.FogData/*[!@]*/