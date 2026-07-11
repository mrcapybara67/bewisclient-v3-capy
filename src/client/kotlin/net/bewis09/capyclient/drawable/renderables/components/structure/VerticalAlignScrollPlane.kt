package net.bewis09.capyclient.drawable.renderables.components.structure

import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.renderables.components.logic.Scrollable
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import org.slf4j.LoggerFactory

class VerticalAlignScrollPlane(val init: (Int) -> List<Renderable>, val gap: Int) : Scrollable(Direction.VERTICAL) {
    constructor(list: List<Renderable>, gap: Int) : this({ list }, gap)

    companion object {
        private val log = LoggerFactory.getLogger("CapyLayoutDebug")
        private var logCounter = 0
    }

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        screenDrawing.enableScissors(x, y, width, height)
        var scrollY = scrollAnimation.get().toInt()
        logCounter++
        val shouldLog = logCounter % 60 == 0
        if (shouldLog && renderables.isNotEmpty()) {
            log.info("[VerticalAlignScrollPlane] rendering {} children at pane=({},{},{},{}), scrollY={}",
                renderables.size, x, y, width, height, scrollY)
        }
        for (i in renderables.indices) {
            val it = renderables[i]
            val prevY = it.y
            it.setPosition(x, y + scrollY)
            it.setWidth(width)
            if (shouldLog) {
                log.info("[VerticalAlignScrollPlane]   child[{}]: class={}, prevY={}, newY={}, height={}, scrollY={}",
                    i, it::class.simpleName, prevY, it.y, it.height, scrollY)
            }
            scrollY += it.height + gap
        }
        innerSize = scrollY - scrollAnimation.get() - gap
        renderRenderables(screenDrawing, mouseX, mouseY)
        screenDrawing.disableScissors()
    }

    override fun init() = init.invoke(width).forEach {
        addRenderable(it)
    }
}