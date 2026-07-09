package net.bewis09.capyclient.features.sidebar

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.capyclient.drawable.renderables.impl.ExtensionListRenderable
import net.bewis09.capyclient.settings.structure.SidebarFeature

object Extensions: SidebarFeature(createIdentifier("capyclient", "extensions"), "Extensions") {
    val extensions = VerticalAlignScrollPlane(APIEntrypointLoader.mapContainer { ExtensionListRenderable(it.provider, it.entrypoint) }, 1)

    override fun getRenderable(): Renderable = extensions
}