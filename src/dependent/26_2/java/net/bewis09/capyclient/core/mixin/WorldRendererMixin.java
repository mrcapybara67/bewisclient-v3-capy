package net.bewis09.capyclient.core.mixin;

import net.bewis09.capyclient.features.utilities.BlockHighlight;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Redirect(method = "submitBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;black(I)I", ordinal = 0))
    private static int drawOutline(int alpha) {
        if (!BlockHighlight.INSTANCE.isEnabled()) return ARGB.black(alpha);

        return (BlockHighlight.INSTANCE.getColor().get().getColorInt() & 0x00FFFFFF) | ((int) (BlockHighlight.INSTANCE.getThickness().get() * 255f) << 24);
    }
}
