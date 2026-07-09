// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.Zoom
import net.bewis09.capyclient.features.utilities.Zoom.factorAnimation
import net.bewis09.capyclient.features.utilities.Zoom.isUsed
import net.bewis09.capyclient.widget.impl.CPSWidget.leftMouseList
import net.bewis09.capyclient.widget.impl.CPSWidget.rightMouseList
import net.minecraft.client.MouseHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(MouseHandler::class)
class MouseMixin {
    // @[1.21.8] "onPress" @[] "onButton"
    @Inject(method = [/*[@]*/"onButton"/*[!@]*/], at = [At("HEAD")])
    // @[1.21.8] button: Int, action: Int, modifiers: Int @[] rawButtonInfo: net.minecraft.client.input.MouseButtonInfo, action: Int
    private fun capyclientOnMouseButton(handle: Long, /*[@]*/rawButtonInfo: net.minecraft.client.input.MouseButtonInfo, action: Int/*[!@]*/, ci: CallbackInfo?) {
        if (action != 1) return

        // @[1.21.8] button @[] rawButtonInfo.button()
        when (/*[@]*/rawButtonInfo.button()/*[!@]*/) {
            0 -> leftMouseList.add(System.currentTimeMillis())
            1 -> rightMouseList.add(System.currentTimeMillis())
        }
    }

    @Inject(method = ["onScroll"], at = [At("HEAD")], cancellable = true)
    private fun capyclientOnMouseScroll(handle: Long, horizontal: Double, vertical: Double, ci: CallbackInfo) {
        // Honor the new "Scroll Wheel Zoom" setting so users who want keyboard-only
        // zoom control can disable wheel fine-tuning without losing the zoom itself.
        if (!isUsed() || !Zoom.scrollEnabled.get()) return

        // Use the user-configured sensitivity / bounds rather than the old
        // hard-coded 0.02 step and 0.009..0.4 coerce range.
        val newFactor = (factorAnimation.getWithoutInterpolation() - vertical.toFloat() * Zoom.scrollSensitivity.get())
            .coerceIn(Zoom.minFactor.get(), Zoom.factor.get())
        factorAnimation.set(newFactor)
        ci.cancel()
    }
}