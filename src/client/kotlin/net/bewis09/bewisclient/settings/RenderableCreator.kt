package net.bewis09.bewisclient.settings

import net.bewis09.bewisclient.drawable.Renderable

interface RenderableCreator<T: Renderable> {
    fun createRenderable(id: String, title: String, description: String? = null): T
}