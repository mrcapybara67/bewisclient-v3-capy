package net.bewis09.bewisclient.features.sidebar

import net.bewis09.bewisclient.api.APIEntrypointLoader
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.CategorizedFeature
import net.bewis09.bewisclient.settings.structure.SidebarFeature

object Utilities: SidebarFeature(createIdentifier("bewisclient", "utilities"), "Utilities") {
    val utilities = APIEntrypointLoader.mapEntrypoint { it.getUtilities() }.flatten().map(CategorizedFeature::createRenderable)

    override fun getRenderable(): Renderable = createGrid(utilities)
}