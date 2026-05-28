package net.bewis09.bewisclient.drawable.renderables.components

import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing

class Rectangle(var color: () -> Color) : Renderable() {
    constructor(color: Color) : this({ color })

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) = screenDrawing.fill(x, y, width, height, color())
}