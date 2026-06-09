package net.bewis09.bewisclient.settings.logic

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.Feature

interface RenderableCreator<T : Renderable> {
    fun createRenderable(feature: Feature, id: String, title: String, description: String? = null): T
}