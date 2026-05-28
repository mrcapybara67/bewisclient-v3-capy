package net.bewis09.bewisclient.core.mixin;

import net.bewis09.bewisclient.features.utilities.PumpkinOverlay;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Inject(method = "renderTextureOverlay", at = @At("HEAD"), cancellable = true)
    public void inject(GuiGraphics guiGraphics, ResourceLocation resourceLocation, float f, CallbackInfo ci) {
        if (PumpkinOverlay.INSTANCE.getEnabled().get() && resourceLocation.toString().equals("minecraft:textures/misc/pumpkinblur.png")) {
            ci.cancel();
        }
    }
}