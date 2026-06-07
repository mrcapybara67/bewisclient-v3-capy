package net.bewis09.bewisclient.mixin.client

import net.bewis09.bewisclient.drawable.BackgroundEffectProvider
import net.bewis09.bewisclient.features.sidebar.General.blurBackground
import net.bewis09.bewisclient.util.logic.ClientInterface
import net.minecraft.client.Options
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Options::class)
class BlurrinessInjectorMixin : ClientInterface {
    @Inject(method = ["getMenuBackgroundBlurriness"], at = [At("RETURN")], cancellable = true)
    private fun bewisclientGetMenuBackgroundBlurrinessValue(cir: CallbackInfoReturnable<Int>) {
        val renderableScreen = getCurrentRenderableScreen() ?: return

        if (!blurBackground.get()) cir.returnValue = 0

        val provider = renderableScreen.renderable as? BackgroundEffectProvider ?: return

        cir.returnValue = (provider.getBackgroundEffectFactor() * cir.returnValue).toInt()
    }
}