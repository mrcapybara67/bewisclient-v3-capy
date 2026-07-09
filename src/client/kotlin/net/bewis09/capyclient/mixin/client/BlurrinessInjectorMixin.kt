package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.drawable.BackgroundEffectProvider
import net.bewis09.capyclient.features.sidebar.General.blurBackground
import net.bewis09.capyclient.util.logic.ClientInterface
import net.minecraft.client.Options
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Options::class)
class BlurrinessInjectorMixin : ClientInterface {
    @Inject(method = ["getMenuBackgroundBlurriness"], at = [At("RETURN")], cancellable = true)
    private fun capyclientGetMenuBackgroundBlurrinessValue(cir: CallbackInfoReturnable<Int>) {
        val renderableScreen = getCurrentRenderableScreen() ?: return

        if (!blurBackground.get()) cir.returnValue = 0

        val provider = renderableScreen.renderable as? BackgroundEffectProvider ?: return

        cir.returnValue = (provider.getBackgroundEffectFactor() * cir.returnValue).toInt()
    }
}