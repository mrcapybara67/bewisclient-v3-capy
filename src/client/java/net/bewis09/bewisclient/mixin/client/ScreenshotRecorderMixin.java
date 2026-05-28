// @VersionReplacement

package net.bewis09.bewisclient.mixin.client;

import net.bewis09.bewisclient.features.screenshot.ScreenshotSettings;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(Screenshot.class)
public class ScreenshotRecorderMixin {
    // @[1.21.4] "method_1664" @[1.21.11] "method_67805" @[26.1.2] "lambda$grab$2" @[] "lambda$grab$4"
    @Inject(method = /*[@]*/"lambda$grab$4"/*[!@]*/, at = @At("HEAD"), cancellable = true)
    private static void injectScreenshotText(File file, Style style, CallbackInfoReturnable<Style> cir) {
        if (ScreenshotSettings.INSTANCE.getRedirect().get())
            // @[1.21.4] (ClickEvent.Action.RUN_COMMAND, "/"+ @[] .RunCommand(
            cir.setReturnValue(style.withClickEvent(new ClickEvent/*[@]*/.RunCommand(/*[!@]*/"bewisclient screenshot "+file.getAbsolutePath())));
    }
}