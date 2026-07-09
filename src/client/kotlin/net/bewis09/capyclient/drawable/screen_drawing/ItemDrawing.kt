package net.bewis09.capyclient.drawable.screen_drawing

import net.bewis09.capyclient.version.drawItem
import net.bewis09.capyclient.version.drawItemOverlay
import net.minecraft.world.item.ItemStack

interface ItemDrawing : ScreenDrawingInterface {
    fun drawItemStack(itemStack: ItemStack, x: Int, y: Int) {
        guiGraphics.drawItem(itemStack, x, y)
    }

    fun drawItemStackWithOverlay(itemStack: ItemStack, x: Int, y: Int) {
        drawItemStack(itemStack, x, y)
        guiGraphics.drawItemOverlay(textRenderer, itemStack, x, y)
    }
}