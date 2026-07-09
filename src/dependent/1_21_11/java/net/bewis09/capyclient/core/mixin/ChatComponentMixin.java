package net.bewis09.capyclient.core.mixin;

import net.bewis09.capyclient.features.utilities.NoChatLag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hard-caps the chat history by silently dropping new messages once the
 * user-configured cap is reached, preventing the per-frame micro-stutters
 * that occur when large multiplayer servers flood the chat with thousands
 * of join/leave/kill messages per tick.
 *
 * Implementation notes:
 *
 *  - The vanilla {@code ChatComponent} in 1.21.11 has two {@code addMessage}
 *    overloads: {@code addMessage(Component)} and
 *    {@code addMessage(Component, MessageSignature, GuiMessageTag)}. An
 *    unqualified {@code @Inject(method = "addMessage")} would be ambiguous
 *    and Mixin would throw {@code InvalidInjectionException} at load time.
 *    We pin the descriptor to the single-arg overload via the full JVM
 *    method descriptor.
 *  - The reset window scales with the configured cap: at least 10 s, and
 *    +200 ms per message in the cap. This keeps a low cap (e.g. 50
 *    messages) from preventing normal sustained chat rates once the burst
 *    is over.
 *  - {@code clearMessages} zeroes the counter only; it does NOT advance
 *    the reset window, so a flood that happens to coincide with a /clear
 *    still hits the cap.
 */
@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Unique
    private int capyclientChatCounter = 0;

    @Unique
    private long capyclientLastResetTime = 0L;

    @Unique
    private int capyclientLastSeenMax = 0;

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), cancellable = true)
    private void capyclientThrottle(Component message, CallbackInfo ci) {
        if (!NoChatLag.INSTANCE.isEnabled()) return;
        int max = NoChatLag.INSTANCE.getMaxMessages().get();
        if (max <= 0) return;

        // Reset window scales with the cap so a small cap doesn't choke
        // sustained chat once the burst ends. 200ms-per-message keeps the
        // minimum throughput at 5 msg/s for any cap; a 10s floor caps the
        // window for very large caps.
        long resetWindowMs = Math.max(10_000L, (long) max * 200L);
        // If the user raised the cap, refresh the window immediately so a
        // generous setting doesn't still have to wait out a 10s hangover
        // from a smaller previous cap.
        if (max != capyclientLastSeenMax) {
            capyclientLastSeenMax = max;
            capyclientChatCounter = 0;
            capyclientLastResetTime = System.currentTimeMillis();
        }

        long now = System.currentTimeMillis();
        if (now - capyclientLastResetTime > resetWindowMs) {
            capyclientChatCounter = 0;
            capyclientLastResetTime = now;
        }

        if (capyclientChatCounter >= max) {
            // Cap reached: drop this newest message so the queue stays flat.
            ci.cancel();
            return;
        }
        capyclientChatCounter++;
    }

    @Inject(method = "clearMessages(Z)V", at = @At("HEAD"))
    private void capyclientOnClear(boolean clearSent, CallbackInfo ci) {
        // Zero only — don't touch the reset window, otherwise a /clear
        // right before a burst would uncap the queue exactly when we need
        // the cap the most.
        capyclientChatCounter = 0;
    }
}
