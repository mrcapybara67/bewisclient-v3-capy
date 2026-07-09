package net.bewis09.capyclient.settings.logic

import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.Feature

interface RenderableCreator<T : Renderable> {
    fun createRenderable(feature: Feature, id: String, title: String, description: String? = null): T
}