package net.bewis09.capyclient.drawable.renderables.notification

import net.bewis09.capyclient.common.Color
import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.drawable.screen_drawing.translate
import net.bewis09.capyclient.features.sidebar.General
import net.minecraft.network.chat.Component

class SimpleTextNotification(val text: Component, val duration: Long = 5000) : Notification() {
    val start = System.currentTimeMillis()

    override val progress: Float
        get() = ((System.currentTimeMillis() - start).toFloat() / duration).coerceIn(0f, 1f)

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        screenDrawing.translate(0L.coerceAtLeast(System.currentTimeMillis() + 500L - start - duration) / 400f * 120, 0f) {
            screenDrawing.setBewisclientFont()
            val lines = screenDrawing.wrapText(text.string, 120)

            internalHeight = lines.size * 9 + 8
            internalWidth = 128 + if (isMinecrafty) 2 else 0

            if (isMinecrafty) {
                SelectiveScreenDrawer.renderButtonBackground(screenDrawing, 0f, 0f, x, y, width + 4, height, 0f, mouseX, mouseY)
            } else {
                screenDrawing.fill(x, y, width, height, Color.BLACK alpha 0.5f)
            }

            lines.forEachIndexed { index, line ->
                screenDrawing.drawText(line, x + if (isMinecrafty) 5 else 4, y + index * 9 + 4, General.getTextThemeColor())
            }

            screenDrawing.fill(x + if (isMinecrafty) 1 else 0, y + height - 1, (width * ((System.currentTimeMillis() - start).toFloat() / duration)).toInt(), 1, General.getThemeColor())
            screenDrawing.setDefaultFont()
        }
    }
}