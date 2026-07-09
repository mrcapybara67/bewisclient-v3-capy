// @VersionReplacement

package net.bewis09.bewisclient.mixin.client;

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
    // CRITICAL FIX: Under Mojang mappings for 1.21.11, the lambda inside
    // `Screenshot.grab(...)` that creates the clickable chat style is named
    // `method_67805` (Yarn intermediary name — Mojang does not provide names
    // for synthetic lambda methods, so the Yarn name is used directly). The
    // previous build used `lambda$grab$1` which does not exist in the Mojang-
    // mapped jar and would throw `InvalidInjectionException` at load time.
    // @[1.21.4] "lambda$grab$4" @[1.21.11] "method_67805" @[26.1.2] "lambda$grab$2" @[] "lambda$grab$4"
    @Inject(method = /*[@]*/"method_67805"/*[!@]*/, at = @At("HEAD"), cancellable = true)
    private static void injectScreenshotText(File file, Style style, CallbackInfoReturnable<Style> cir) {
        if (net.bewis09.bewisclient.features.sidebar.Screenshot.INSTANCE.getRedirect().get())
            // @[1.21.4] (ClickEvent.Action.RUN_COMMAND, "/"+ @[] .RunCommand(
            cir.setReturnValue(style.withClickEvent(new ClickEvent/*[@]*/.RunCommand(/*[!@]*/"bewisclient screenshot "+file.getAbsolutePath())));
    }
}