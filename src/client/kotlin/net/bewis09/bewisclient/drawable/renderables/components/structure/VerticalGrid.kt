package net.bewis09.bewisclient.drawable.renderables.components.structure

import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.components.logic.Scrollable
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import kotlin.math.floor

open class VerticalGrid(val init: (Int) -> List<Renderable>, val gap: Int, val minElementWidth: Int) : Renderable() {
    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        if (renderables.isEmpty()) return

        val elementsInRow = floor(((width + gap) / (minElementWidth + gap)).toDouble()).coerceAtLeast(1.0)
        val elementWidth = (width + gap) / elementsInRow - gap
        val columnHeights = Array(elementsInRow.toInt()) { 0 }

        screenDrawing.enableScissors(x, y, width, height)
        for (it in ArrayList(renderables)) {
            val min = columnHeights.minOrNull() ?: 0
            val columnIndex = columnHeights.indexOf(min)

            it.setWidth(elementWidth.toInt())
            it.setPosition(x + (columnIndex * (elementWidth + gap)).toInt(), y + min)
            columnHeights[columnIndex] += it.height + gap
        }
        internalHeight = columnHeights.max() - gap
        renderRenderables(screenDrawing, mouseX, mouseY)
        screenDrawing.disableScissors()
    }

    override fun init() {
        val elementsInRow = floor(((width + gap) / (minElementWidth + gap)).toDouble())
        val elementWidth = (width + gap) / elementsInRow - gap

        init.invoke(elementWidth.toInt()).forEach {
            addRenderable(it)
        }
    }
}