package net.bewis09.capyclient.features.sidebar

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.CategorizedFeature
import net.bewis09.capyclient.settings.structure.SidebarFeature

object Utilities: SidebarFeature(createIdentifier("capyclient", "utilities"), "Utilities") {
    val utilities = APIEntrypointLoader.mapEntrypoint { it.getUtilities() }.flatten().map(CategorizedFeature::createRenderable)

    override fun getRenderable(): Renderable = createGrid(utilities)
}