package net.bewis09.capyclient.drawable.renderables.popup

import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.renderables.components.button.Button
import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.features.sidebar.General
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component

class ConfirmPopup(val text: Component, val onConfirm: () -> Unit, confirmText: Component = CommonComponents.GUI_CONTINUE, cancelText: Component = CommonComponents.GUI_CANCEL) : Renderable(200, 20) {
    val cancelButton = Button(cancelText) {
        OptionScreen.currentInstance?.closePopup()
    }

    val confirmButton = Button(confirmText, selected = { true }, onClick = {
        onConfirm()
        OptionScreen.currentInstance?.closePopup()
    })

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        val lines = screenDrawing.wrapText(text.string, width - 20)
        internalHeight = 40 + lines.size * 9

        cancelButton.setSize((width - 18) / 2, SelectiveScreenDrawer.getSideButtonHeight())
        confirmButton.setSize((width - 18) / 2, SelectiveScreenDrawer.getSideButtonHeight())

        cancelButton.setPosition(x + 6, y + height - SelectiveScreenDrawer.getSideButtonHeight() - 6)
        confirmButton.setPosition(x + width - confirmButton.width - 6, y + height - SelectiveScreenDrawer.getSideButtonHeight() - 6)

        SelectiveScreenDrawer.renderPopupBackground(screenDrawing, x, y, width, height, 5, 0.3f)

        lines.forEachIndexed { index, line ->
            screenDrawing.drawCenteredText(line, x + width / 2, y + 10 + index * 9, General.getTextThemeColor())
        }

        renderRenderables(screenDrawing, mouseX, mouseY)
    }

    override fun init() {
        addRenderable(cancelButton)
        addRenderable(confirmButton)
    }
}