package net.bewis09.bewisclient.drawable.renderables.notification

import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.drawable.screen_drawing.translate
import net.bewis09.bewisclient.impl.settings.GeneralSettings
import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.minecraft.network.chat.Component

class ProgressNotification(val text: Component): Notification() {
    override var progress: Float = 0f
        set(value) {
            if (value >= 1f) {
                field = -1f
                removeStartTime = System.currentTimeMillis() + 500L
                return
            }
            field = value
        }
        get() {
            return if (System.currentTimeMillis() - removeStartTime > 400) 1f else field
        }

    var removeStartTime = Long.MAX_VALUE

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        screenDrawing.translate(((System.currentTimeMillis() - removeStartTime) / 400f).coerceIn(0f, 1f) * 120,0f) {
            screenDrawing.setBewisclientFont()
            val lines = screenDrawing.wrapText(text.string, 120).map(Component::literal) + Component.literal("${((if (progress == -1f) 1f else progress) * 100).toInt()}%").withColor(Color.GRAY.argb)

            internalHeight = lines.size * 9 + 8
            internalWidth = 128 + if(isMinecrafty) 2 else 0

            if (isMinecrafty) {
                SelectiveScreenDrawer.renderButtonBackground(screenDrawing, 0f, 0f, x, y, width + 4, height, 0f, false, mouseX, mouseY)
            } else {
                screenDrawing.fill(x, y, width, height, Color.BLACK alpha 0.5f)
            }

            lines.forEachIndexed { index, line ->
                screenDrawing.drawText(line, x + if (isMinecrafty) 5 else 4, y + index * 9 + 4, GeneralSettings.getTextThemeColor())
            }

            screenDrawing.fill(x + if (isMinecrafty) 1 else 0, y + height - 1, (width * (if (progress == -1f) 1f else progress)).toInt(), 1, GeneralSettings.getThemeColor())
            screenDrawing.setDefaultFont()
        }
    }
}