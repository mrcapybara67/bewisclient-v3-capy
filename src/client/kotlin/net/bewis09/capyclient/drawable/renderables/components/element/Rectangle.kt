package net.bewis09.capyclient.drawable.renderables.components.element

import net.bewis09.capyclient.common.Color
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing

class Rectangle(var color: () -> Color) : Renderable() {
    constructor(color: Color) : this({ color })

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) = screenDrawing.fill(x, y, width, height, color())
}