// @VersionReplacement

package net.bewis09.bewisclient.mixin.client

import net.bewis09.bewisclient.features.utilities.Zoom.getFactor
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

// @[1.21.11] net.minecraft.client.renderer.GameRenderer @[] net.minecraft.client.Camera
@Mixin(/*[@]*/net.minecraft.client.renderer.GameRenderer/*[!@]*/::class)
class ZoomMixin {
    // @[1.21.11] "getFov" @[] "modifyFovBasedOnDeathOrFluid"
    @Inject(method = [/*[@]*/"getFov"/*[!@]*/], at = [At("RETURN")], cancellable = true)
    // @[1.21.1] camera: net.minecraft.client.Camera, tickProgress: Float, changingFov: Boolean, cir: CallbackInfoReturnable<Double> @[1.21.11] camera: net.minecraft.client.Camera, tickProgress: Float, changingFov: Boolean, cir: CallbackInfoReturnable<Float> @[] cir: CallbackInfoReturnable<Float>
    fun inject(/*[@]*/camera: net.minecraft.client.Camera, tickProgress: Float, changingFov: Boolean, cir: CallbackInfoReturnable<Float>/*[!@]*/) {
        cir.returnValue = cir.returnValue!! * getFactor()
    }
}