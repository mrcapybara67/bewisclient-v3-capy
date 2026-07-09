package net.bewis09.bewisclient.core.mixin;

import net.bewis09.bewisclient.features.utilities.HeldItemTooltip;
import net.bewis09.bewisclient.widget.impl.InventoryWidget;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract @Nullable Component getCustomName();

    @Inject(method = "addToTooltip", at = @At("HEAD"))
    private <T extends TooltipProvider> void bewisclient$appendComponentTooltip(DataComponentType<@NotNull T> dataComponentType, Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (HeldItemTooltip.INSTANCE.isLookup()) {
            HeldItemTooltip.INSTANCE.getComponentSet().add(dataComponentType);
        }
    }

    /**
     * BUGFIX 1.21.11: In 1.21.11, {@code addAttributeTooltips} was refactored
     * to delegate to {@code forEachModifier} via a TriConsumer invokedynamic,
     * so it no longer calls {@code Consumer.accept(Object)} directly.
     * The old @Redirect targeting Consumer.accept inside addAttributeTooltips
     * would find ZERO call sites and crash the game with "Scanned 0 target(s)".
     *
     * We now intercept at addDetailsToTooltip (the public entry point for
     * tooltip details) instead, wrapping all tooltip addition in a guard:
     * when HeldItemTooltip is rendering its own overlay, we cancel the vanilla
     * attribute-modifier lines by injecting a transparent consumer wrapper.
     *
     * The {@code require = 0} on the redirect means Mixin won't crash even if
     * the target dispatch changes in a future snapshot — the feature simply
     * degrades gracefully (attribute lines show in the custom overlay).
     */
    @Redirect(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"), require = 0)
    private static <T> void bewisclient$appendAttributeModifiersTooltip(Consumer<T> instance, T o) {
        if (!HeldItemTooltip.INSTANCE.isRendering()) instance.accept(o);
    }

    @Inject(method = "isEnchanted", at = @At("HEAD"), cancellable = true)
    private void bewisclient$hasEnchantments(CallbackInfoReturnable<Boolean> cir) {
        if (this.getCustomName() == InventoryWidget.INSTANCE.getIndicatorText()) cir.setReturnValue(true);
    }
}