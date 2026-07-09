package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.game.BewisclientResourcePack
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.FolderRepositorySource
import net.minecraft.server.packs.repository.Pack
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.function.Consumer

@Mixin(FolderRepositorySource::class)
class BewisclientPackProviderMixin {
    @Shadow
    @Final
    private val packType: PackType? = null

    @Inject(method = ["loadPacks"], at = [At("HEAD")])
    private fun loadPacks(profileAdder: Consumer<Pack?>, ci: CallbackInfo?) {
        if (packType != PackType.CLIENT_RESOURCES) return

        profileAdder.accept(BewisclientResourcePack.pack)
    }
}