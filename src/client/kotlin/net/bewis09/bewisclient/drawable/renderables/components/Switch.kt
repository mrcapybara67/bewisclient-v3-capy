package net.bewis09.bewisclient.drawable.renderables.components

import net.bewis09.bewisclient.drawable.Animator
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.util.interfaces.Gettable
import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer

class Switch(var state: Gettable<Boolean>, val onChange: (new: Boolean) -> Unit) : Hoverable(24, 12) {
    val stateAnimation = Animator({ animationDuration }, Animator.EASE_IN_OUT, if (state.get()) 1f else 0f)

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)

        stateAnimation.set(if (state.get()) 1f else 0f)

        SelectiveScreenDrawer.renderSwitch(screenDrawing, x, y, width, height, hoverFactor, stateAnimation.get(), mouseX, mouseY)
    }

    override fun init() {
        stateAnimation.pauseForOnce()
        super.init()
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean = onChange(!state.get()).let { true }
}