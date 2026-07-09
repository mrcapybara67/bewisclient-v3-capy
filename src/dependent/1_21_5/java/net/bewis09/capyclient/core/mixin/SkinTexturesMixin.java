package net.bewis09.capyclient.core.mixin;

import net.bewis09.capyclient.cosmetics.CosmeticType;
import net.bewis09.capyclient.features.cosmetics.Cosmetic;
import net.bewis09.capyclient.features.cosmetics.CosmeticLoader;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerSkin.class)
public abstract class SkinTexturesMixin {
    @Shadow
    public abstract int hashCode();

    @Inject(method = "capeTexture", at = @At("HEAD"), cancellable = true)
    private void capyclient$cape(CallbackInfoReturnable<ResourceLocation> cir) {
        PlayerInfo playerListEntry = CosmeticLoader.INSTANCE.getEntityBySkinTextures(this.hashCode());
        if (playerListEntry == null) return;
        Cosmetic cosmetic = CosmeticLoader.INSTANCE.getCosmeticForPlayer(playerListEntry.getProfile(), CosmeticType.CAPE);
        if (cosmetic != null) {
            cir.setReturnValue(cosmetic.getIdentifier());
        }
    }
}
