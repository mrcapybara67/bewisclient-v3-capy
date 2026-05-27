// @VersionReplacement

package net.bewis09.bewisclient.drawable

import net.bewis09.bewisclient.version.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

open class IndependentScreen(title: Component): Screen(title) {
    // @[1.21.8] (mouseX: Double, mouseY: Double, button: Int): Boolean = onMouseClick(mouseX, mouseY, button) @[] (click: net.minecraft.client.input.MouseButtonEvent, doubled: Boolean): Boolean = onMouseClick(click.x, click.y, click.button())
    override fun mouseClicked/*[@]*/(click: net.minecraft.client.input.MouseButtonEvent, doubled: Boolean): Boolean = onMouseClick(click.x, click.y, click.button())/*[!@]*/
    // @[1.21.8] (mouseX: Double, mouseY: Double, button: Int): Boolean = onMouseRelease(mouseX, mouseY, button) @[] (click: net.minecraft.client.input.MouseButtonEvent): Boolean = onMouseRelease(click.x, click.y, click.button())
    override fun mouseReleased/*[@]*/(click: net.minecraft.client.input.MouseButtonEvent): Boolean = onMouseRelease(click.x, click.y, click.button())/*[!@]*/
    // @[1.21.8] (mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean = onMouseDrag(mouseX, mouseY, deltaX, deltaY, button) @[] (click: net.minecraft.client.input.MouseButtonEvent, offsetX: Double, offsetY: Double): Boolean = onMouseDrag(click.x, click.y, offsetX, offsetY, click.button())
    override fun mouseDragged/*[@]*/(click: net.minecraft.client.input.MouseButtonEvent, offsetX: Double, offsetY: Double): Boolean = onMouseDrag(click.x, click.y, offsetX, offsetY, click.button())/*[!@]*/
    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean = onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount)
    // @[1.21.8] (keyCode: Int, scanCode: Int, modifiers: Int): Boolean = onKeyPress(keyCode, scanCode, modifiers) @[] (input: net.minecraft.client.input.KeyEvent): Boolean = onKeyPress(input.key, input.scancode, input.modifiers)
    override fun keyPressed/*[@]*/(input: net.minecraft.client.input.KeyEvent): Boolean = onKeyPress(input.key, input.scancode, input.modifiers)/*[!@]*/
    // @[1.21.8] (keyCode: Int, scanCode: Int, modifiers: Int): Boolean = onKeyRelease(keyCode, scanCode, modifiers) @[] (input: net.minecraft.client.input.KeyEvent): Boolean = onKeyRelease(input.key, input.scancode, input.modifiers)
    override fun keyReleased/*[@]*/(input: net.minecraft.client.input.KeyEvent): Boolean = onKeyRelease(input.key, input.scancode, input.modifiers)/*[!@]*/
    // @[1.21.8] (chr: Char, modifiers: Int): Boolean = onCharTyped(chr, modifiers) @[] (input: net.minecraft.client.input.CharacterEvent): Boolean = onCharTyped(input.codepoint.toChar(), 0)
    override fun charTyped/*[@]*/(input: net.minecraft.client.input.CharacterEvent): Boolean = onCharTyped(input.codepoint.toChar(), 0)/*[!@]*/

    open fun onMouseClick(x: Double, y: Double, button: Int): Boolean = false
    open fun onMouseRelease(x: Double, y: Double, button: Int): Boolean = false
    open fun onMouseDrag(x: Double, y: Double, deltaX: Double, deltaY: Double, button: Int): Boolean = false
    open fun onMouseScroll(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean = false
    open fun onKeyPress(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false
    open fun onKeyRelease(keyCode: Int, scanCode: Int, modifiers: Int): Boolean = false
    open fun onCharTyped(chr: Char, modifiers: Int): Boolean = false

    // @[1.21.11] render @[] extractRenderState
    override fun /*[@]*/render/*[!@]*/(graphics: GuiGraphics, mouseX: Int, mouseY: Int, deltaTicks: Float) = this.render(graphics, mouseX, mouseY)

    open fun render(context: GuiGraphics, mouseX: Int, mouseY: Int) = Unit

    // @[1.21.11] renderMenuBackground @[] extractMenuBackground
    open fun renderBackground(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int) = super./*[@]*/renderMenuBackground/*[!@]*/(guiGraphics, x, y, width, height)

    @Suppress("unused")
    // @[1.21.5] = super.renderBackground(context, mouseX, mouseY, deltaTicks) @[] {}
    fun renderIndependentBackground(context: GuiGraphics, mouseX: Int, mouseY: Int, deltaTicks: Float) /*[@]*/{}/*[!@]*/
}