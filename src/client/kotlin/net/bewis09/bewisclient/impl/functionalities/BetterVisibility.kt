package net.bewis09.bewisclient.impl.functionalities

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.game.Translation
import net.bewis09.bewisclient.settings.structure.ImageFeature

object BetterVisibility : ImageFeature("better_visibility", Translation("menu.category.better_visibility", "Better Visibility")) {
    val nether = boolean("nether", false)
    val water = boolean("water", false)
    val lava = boolean("lava", false)
    val powder_snow = boolean("powder_snow", false)

    override val settingRenderables: Array<Renderable> = arrayOf(
        nether.createRenderable("better_visibility.nether", "Nether", "Improve visibility in the Nether dimension").addToQuickSettings("menu.category.better_visibility", "nether"),
        water.createRenderable("better_visibility.water", "Water", "Enhance visibility underwater").addToQuickSettings("menu.category.better_visibility", "water"),
        lava.createRenderable("better_visibility.lava", "Lava", "Boost visibility in lava").addToQuickSettings("menu.category.better_visibility", "lava"),
        powder_snow.createRenderable("better_visibility.powder_snow", "Powder Snow", "Increase visibility in powder snow").addToQuickSettings("menu.category.better_visibility", "snow")
    )
}