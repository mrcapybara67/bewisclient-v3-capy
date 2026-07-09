package net.bewis09.capyclient.drawable.draw_methods

import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing

interface DrawMethods {
    fun renderMenuBackground(screenDrawing: ScreenDrawing, screenWidth: Int, screenHeight: Int)

    fun renderButtonBackground(screenDrawing: ScreenDrawing, hover: Float, animation: Float, x: Int, y: Int, width: Int, height: Int, click: Float, mouseX: Int, mouseY: Int, dark: Boolean = false, small: Boolean = false)

    fun getSideButtonHeight(): Int

    fun renderSwitch(screenDrawing: ScreenDrawing, x: Int, y: Int, width: Int, height: Int, hover: Float, stateAnimation: Float, mouseX: Int, mouseY: Int)

    fun renderFader(screenDrawing: ScreenDrawing, x: Int, y: Int, width: Int, height: Int, hover: Float, normalizedValue: Float, mouseX: Int, mouseY: Int)

    fun renderSettingsCategoryBackground(screenDrawing: ScreenDrawing, x: Int, y: Int, width: Int, height: Int, state: Float, hover: Float, mouseX: Int, mouseY: Int)

    fun renderPopupBackground(screenDrawing: ScreenDrawing, x: Int, y: Int, width: Int, height: Int, borderRadius: Int, borderAlpha: Float)
}