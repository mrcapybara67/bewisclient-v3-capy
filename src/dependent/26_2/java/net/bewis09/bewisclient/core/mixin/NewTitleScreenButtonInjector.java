package net.bewis09.bewisclient.core.mixin;

import net.bewis09.bewisclient.common.UtilKt;
import net.bewis09.bewisclient.drawable.minecraft.TexturedButtonWidget;
import net.bewis09.bewisclient.drawable.renderables.screen.OptionScreen;
import net.bewis09.bewisclient.settings.impl.GeneralSettings;
import net.bewis09.bewisclient.drawable.minecraft.RenderableScreen;
import net.bewis09.bewisclient.version.VersionCoreKt;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public abstract class NewTitleScreenButtonInjector extends Screen {
    @Shadow
    protected abstract int getHorizontalPosition(int currentButton, int numberOfButtons, int buttonWidth);

    protected NewTitleScreenButtonInjector(Component title) {
        super(title);
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;getHorizontalPosition(III)I"), index = 1)
    private int buttonNumber(int original) {
        return original + 1;
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/SpriteIconButton;setPosition(II)V", ordinal = 1))
    public void addButton(SpriteIconButton instance, int i, int j) {
        if (GeneralSettings.INSTANCE.getButtonInTitleScreen().get()) {
            TexturedButtonWidget button = addRenderableWidget(new TexturedButtonWidget(
                    this.width / 2 + 106,
                    this.height / 4 + 56,
                    20,
                    20,
                    UtilKt.createIdentifier("bewisclient", "textures/gui/sprites/options_button.png"),
                    UtilKt.createIdentifier("bewisclient", "textures/gui/sprites/options_button_pressed.png"),
                    (b) -> VersionCoreKt.setScreen(new RenderableScreen(new OptionScreen(1f, 0f)))
            ));

            button.setTooltip(Tooltip.create(Component.literal("Bewisclient")));

            button.setPosition(this.getHorizontalPosition(4, 4, 20), j);
        }
        instance.setPosition(i, j);
    }
}
