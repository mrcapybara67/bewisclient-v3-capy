// @VersionReplacement

package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature
import net.bewis09.capyclient.settings.types.BooleanSetting

object BetterVisibility : ImageFeature(createIdentifier("capyclient", "better_visibility"), "Better Visibility") {
    val nether = boolean("nether", false)
    val water = boolean("water", false)
    val lava = boolean("lava", false)
    val powder_snow = boolean("powder_snow", false)

    val netherMultiplier = float("nether_multiplier", 2.0f, 0.5f, 4.0f, 0.1f, 2)
    val waterMultiplier = float("water_multiplier", 1.0f, 0.5f, 4.0f, 0.1f, 2)
    val lavaMultiplier = float("lava_multiplier", 2.0f, 0.5f, 4.0f, 0.1f, 2)
    val powderSnowMultiplier = float("powder_snow_multiplier", 2.0f, 0.5f, 4.0f, 0.1f, 2)

    val reduceLavaRedness = boolean("reduce_lava_redness", false)
    val reduceWaterBlue = boolean("reduce_water_blue", false)
    val removePowderSnowTint = boolean("remove_powder_snow_tint", false)
    val disableNetherFog = boolean("disable_nether_fog", false)

    class FogModifierConfig(
        val setting: BooleanSetting,
        val start: (Float) -> Float,
        val end: (Float) -> Float,
        val distanceMultiplier: () -> Float
    )

    val fogModifiers = mapOf(
        "atmospheric" to FogModifierConfig(nether, { it * 2 - (it / 10.0f).coerceIn(4.0f, 64.0f) }, { it * 2 }, { netherMultiplier.get() }),
        "water" to FogModifierConfig(water, { -8f }, { it }, { waterMultiplier.get() }),
        "lava" to FogModifierConfig(lava, { -8f }, { 16f }, { lavaMultiplier.get() }),
        "powder_snow" to FogModifierConfig(powder_snow, { -8f }, { 8f }, { powderSnowMultiplier.get() })
    )

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, nether, "nether", "Nether", "Improve visibility in the Nether dimension", "nether")
        list.addRenderable(this, water, "water", "Water", "Enhance visibility underwater", "water")
        list.addRenderable(this, lava, "lava", "Lava", "Boost visibility in lava", "lava")
        list.addRenderable(this, powder_snow, "powder_snow", "Powder Snow", "Increase visibility in powder snow", "snow")
        list.addRenderable(this, netherMultiplier, "nether_multiplier",
            "Nether Fog Multiplier",
            "Multiplier for the nether atmospheric fog distance when enabled. 1.0 = vanilla, 2.0 = 2x as far.",
            "nether_multiplier"
        )
        list.addRenderable(this, waterMultiplier, "water_multiplier",
            "Water Fog Multiplier",
            "Multiplier for the underwater fog distance when enabled.",
            "water_multiplier"
        )
        list.addRenderable(this, lavaMultiplier, "lava_multiplier",
            "Lava Fog Multiplier",
            "Multiplier for the lava fog distance when enabled.",
            "lava_multiplier"
        )
        list.addRenderable(this, powderSnowMultiplier, "powder_snow_multiplier",
            "Powder Snow Fog Multiplier",
            "Multiplier for the powder-snow fog distance when enabled.",
            "powder_snow_multiplier"
        )
        list.addRenderable(this, disableNetherFog, "disable_nether_fog",
            "Disable Nether Fog",
            "When 'Nether' is on, also remove the orange tint from the fog.",
            "disable_nether_fog"
        )
        list.addRenderable(this, reduceWaterBlue, "reduce_water_blue",
            "Reduce Water Blue Tint",
            "When 'Water' is on, also reduce the blue color shift underwater.",
            "reduce_water_blue"
        )
        list.addRenderable(this, reduceLavaRedness, "reduce_lava_redness",
            "Reduce Lava Red Tint",
            "When 'Lava' is on, also reduce the red color shift in lava.",
            "reduce_lava_redness"
        )
        list.addRenderable(this, removePowderSnowTint, "remove_powder_snow_tint",
            "Remove Powder Snow Tint",
            "When 'Powder Snow' is on, also remove the white tint from the fog.",
            "remove_powder_snow_tint"
        )
    }

    fun applyFogModifier(instance: String, fogData: FogData, viewDistance: Float) {
        if (!isEnabled()) return

        val cfg: FogModifierConfig = when (instance) {
            "atmospheric" -> fogModifiers.getValue("atmospheric")
            "water" -> fogModifiers.getValue("water")
            "lava" -> fogModifiers.getValue("lava")
            "powder_snow" -> fogModifiers.getValue("powder_snow")
            else -> return
        }

        if (!cfg.setting.get()) return

        val baseEnd = cfg.end(viewDistance)
        val scaledEnd = baseEnd * cfg.distanceMultiplier()
        if (scaledEnd <= fogData.environmentalEnd) return

        fogData.environmentalEnd = scaledEnd
        fogData.environmentalStart = cfg.start(viewDistance)
    }
}

// @[1.21.5] class FogData(var environmentalStart: Float, var environmentalEnd: Float) @[] typealias FogData = net.minecraft.client.renderer.fog.FogData
/*[@]*/typealias FogData = net.minecraft.client.renderer.fog.FogData/*[!@]*/