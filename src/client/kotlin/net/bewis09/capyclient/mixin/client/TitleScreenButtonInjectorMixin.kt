package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.common.getModrinthVersion
import net.bewis09.capyclient.drawable.minecraft.TexturedButtonWidget
import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.features.sidebar.General.buttonInTitleScreen
import net.bewis09.capyclient.util.logic.ClientInterface
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Redirect

@Mixin(TitleScreen::class)
class TitleScreenButtonInjectorMixin(title: Component) : Screen(title), ClientInterface {
    @Redirect(method = ["createNormalMenuOptions"], at = At(value = "INVOKE", target = $$"Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;", ordinal = 1))
    private fun capyclientInit(instance: Button.Builder, x: Int, y: Int, width: Int, height: Int): Button.Builder {
        if (buttonInTitleScreen.get() && getModrinthVersion() < "26.2") this.addRenderableWidget(TexturedButtonWidget(x + width + 4, y, 20, 20, createIdentifier("capyclient", "textures/gui/sprites/options_button.png"), createIdentifier("capyclient", "textures/gui/sprites/options_button_pressed.png")) { setRenderableScreen(OptionScreen()) })
        return instance.bounds(x, y, width, height)
    }
}