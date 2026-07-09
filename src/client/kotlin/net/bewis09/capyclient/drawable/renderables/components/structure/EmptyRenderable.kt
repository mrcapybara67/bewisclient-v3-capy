package net.bewis09.capyclient.drawable.renderables.components.structure

import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing

class EmptyRenderable : Renderable() {
    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) = Unit
}