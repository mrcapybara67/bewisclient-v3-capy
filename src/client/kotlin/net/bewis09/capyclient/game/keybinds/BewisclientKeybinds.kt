package net.bewis09.capyclient.game.keybinds

import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.util.Bewisclient
import org.lwjgl.glfw.GLFW

val OpenOptionScreen = Keybind(GLFW.GLFW_KEY_RIGHT_SHIFT, "open_option_screen", "Open Capy Client Option Screen") {
    Bewisclient.setRenderableScreen(OptionScreen())
}