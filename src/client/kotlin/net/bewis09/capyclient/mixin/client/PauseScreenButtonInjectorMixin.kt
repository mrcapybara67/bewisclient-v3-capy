package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.common.getModrinthVersion
import net.bewis09.capyclient.drawable.minecraft.TexturedButtonWidget
import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.features.sidebar.General.buttonInGameScreen
import net.bewis09.capyclient.util.logic.ClientInterface
import net.minecraft.client.gui.screens.PauseScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(PauseScreen::class)
class PauseScreenButtonInjectorMixin(title: Component) : Screen(title), ClientInterface {
    @Inject(method = ["init"], at = [At("HEAD")])
    private fun capyclientInit(ci: CallbackInfo?) {
        if (buttonInGameScreen.get() && getModrinthVersion() < "26.2") addRenderableWidget(
            TexturedButtonWidget(
                this.width / 2 + 106,
                this.height / 4 + 57,
                20,
                20,
                createIdentifier("capyclient", "textures/gui/sprites/options_button.png"),
                createIdentifier("capyclient", "textures/gui/sprites/options_button_pressed.png")
            ) { setRenderableScreen(OptionScreen(1f)) }
        )
    }
}