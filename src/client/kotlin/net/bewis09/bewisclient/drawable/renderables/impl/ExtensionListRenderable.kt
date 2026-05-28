package net.bewis09.bewisclient.drawable.renderables.impl

import net.bewis09.bewisclient.api.BewisclientAPIEntrypoint
import net.bewis09.bewisclient.common.*
import net.bewis09.bewisclient.drawable.Animator
import net.bewis09.bewisclient.drawable.renderables.settings.SettingRenderable
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.fabricmc.loader.api.ModContainer
import kotlin.math.roundToInt

class ExtensionListRenderable(val modContainer: ModContainer, val entrypoint: BewisclientAPIEntrypoint) : SettingRenderable(null, 22) {
    val notFoundIdentifier: Identifier = createIdentifier("textures/misc/unknown_pack.png")

    val menuAnimation = Animator({ animationDuration }, Animator.Companion.EASE_IN_OUT, 0f)

    override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
        super.render(screenDrawing, mouseX, mouseY)
        screenDrawing.push()
        screenDrawing.translate(0f, 11 - screenDrawing.getTextHeight() / 2f + 0.5f)
        screenDrawing.drawText(("${entrypoint.getExtensionTitle(modContainer)} ").toText().append(("(${modContainer.metadata.id})").toText().setColor(0xAAAAAA)), x + 32, y, Color.Companion.WHITE)
        val lines = screenDrawing.drawWrappedText(entrypoint.getExtensionDescription(modContainer), x + 32, y + 10, width - 40, 0xAAAAAA.color alpha 0.8f)
        screenDrawing.pop()
        screenDrawing.drawTexture(entrypoint.getIcon(modContainer) ?: notFoundIdentifier, x + 8, centerY - 8, 0f, 0f, 16, 16, 16, 16)
        internalHeight = 22 + lines.size * 9 + 1 + (menuAnimation.get() * 19).roundToInt()
    }
}