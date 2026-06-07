package net.bewis09.bewisclient.features.sidebar

import net.bewis09.bewisclient.api.APIEntrypointLoader
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.bewisclient.drawable.renderables.impl.ExtensionListRenderable
import net.bewis09.bewisclient.settings.structure.SidebarFeature

object Extensions: SidebarFeature(createIdentifier("bewisclient", "extensions"), "Extensions") {
    val extensions = VerticalAlignScrollPlane(APIEntrypointLoader.mapContainer { ExtensionListRenderable(it.provider, it.entrypoint) }, 1)

    override fun getRenderable(): Renderable = extensions
}