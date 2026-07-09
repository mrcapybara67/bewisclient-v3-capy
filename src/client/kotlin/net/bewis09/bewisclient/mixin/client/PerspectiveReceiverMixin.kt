package net.bewis09.bewisclient.mixin.client

import net.bewis09.bewisclient.features.utilities.Perspective
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Entity::class)
class PerspectiveReceiverMixin {
    @Inject(method = ["turn"], at = [At("HEAD")], cancellable = true)
    fun inject(cursorDeltaX: Double, cursorDeltaY: Double, ci: CallbackInfo) {
        if (!Perspective.isEnabled()) return
        val mc = Minecraft.getInstance()
        if (mc.options.cameraType.isFirstPerson) return

        // Determine whether perspective is "active" this frame. Two modes:
        //  - holdToToggle=true (default): active only while the key is physically down
        //  - holdToToggle=false:        active when the toggle flag is set
        val isActive = if (Perspective.holdToToggle.get()) {
            Perspective.EnablePerspective.isPressed()
        } else {
            Perspective.perspectiveActive
        }
        if (!isActive) {
            // restoreOnDisable: snap rotation back to 0 when the key is released
            // / toggle turned off. Without this, the camera would stay where
            // it was at the moment of release.
            if (Perspective.cameraAddPitch != 0f || Perspective.cameraAddYaw != 0f) {
                if (Perspective.restoreOnDisable.get()) {
                    Perspective.cameraAddPitch = 0f
                    Perspective.cameraAddYaw = 0f
                }
            }
            return
        }

        val sens = Perspective.sensitivity.get().toDouble()
        Perspective.cameraAddPitch = Mth.clamp(
            (Perspective.cameraAddPitch + cursorDeltaY * sens).toFloat(),
            -Perspective.maxPitch.get(),
            Perspective.maxPitch.get()
        )
        Perspective.cameraAddYaw = Mth.clamp(
            (Perspective.cameraAddYaw + cursorDeltaX * sens).toFloat(),
            -Perspective.maxYaw.get(),
            Perspective.maxYaw.get()
        )
        ci.cancel()
    }
}
