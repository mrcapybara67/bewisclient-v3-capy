package net.bewis09.capyclient.drawable.renderables.components.setting

import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.renderables.components.logic.Hoverable
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.util.interfaces.Gettable
import net.bewis09.capyclient.util.number.Precision

class Fader(val value: Gettable<Float>, val precision: Precision, val onChange: (new: Float) -> Unit) : Hoverable(100, 14) {
    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        val normalizedValue = precision.normalize(value.get())
        SelectiveScreenDrawer.renderFader(screenDrawing, x, y, width, height, hoverAnimation.get(), normalizedValue, mouseX, mouseY)
    }

    override fun onMouseDrag(mouseX: Double, mouseY: Double, startX: Double, startY: Double, button: Int): Boolean {
        return onMouseClick(mouseX, mouseY, button)
    }

    override fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val relativeX = mouseX - x - 4
        var newValue = precision.denormalize(
            (relativeX / (width - 8)).coerceIn(0.0, 1.0).toFloat()
        )
        newValue = precision.getNearestStep(newValue)
        newValue = precision.round(newValue)
        if (newValue == value.get()) return true
        onChange(newValue)
        return true
    }
}