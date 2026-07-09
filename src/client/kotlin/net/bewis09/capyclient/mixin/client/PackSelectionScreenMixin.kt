package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.minecraft.TexturedButtonWidget
import net.bewis09.capyclient.drawable.renderables.screen.PackListScreen
import net.bewis09.capyclient.features.utilities.PackAdder
import net.bewis09.capyclient.server.Modrinth
import net.bewis09.capyclient.util.Bewisclient
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.packs.PackSelectionScreen
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyArg
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.nio.file.Path

@Mixin(PackSelectionScreen::class)
class PackSelectionScreenMixin(title: Component) : Screen(title) {
    @Shadow
    @Final
    private val packDir: Path? = null

    @Unique
    var addResourcePackButton: Button? = null

    @Unique
    var buttonTexture: Identifier = createIdentifier("capyclient", "textures/gui/sprites/pack_screen_button_texture.png")

    @Inject(method = ["init"], at = [At("RETURN")])
    fun capyclientInit(ci: CallbackInfo) {
        if (!PackAdder.isEnabled()) return

        addResourcePackButton = addRenderableWidget(TexturedButtonWidget(width / 2 - 215, height - 49, 200, 18, buttonTexture, buttonTexture, { b: Button? ->
            Bewisclient.setRenderableScreen(
                PackListScreen(
                    if (packDir.endsWith(Path.of("resourcepacks"))) Modrinth.Type.RESOURCE_PACK else Modrinth.Type.DATA_PACK, this, this.packDir
                )
            )
        }, (if (packDir!!.endsWith(Path.of("resourcepacks"))) Modrinth.addResourcePackText.getTranslatedText() else Modrinth.addDataPackText.getTranslatedText()).append("...")))
    }

    @Inject(method = ["repositionElements"], at = [At("HEAD")])
    fun capyclientRefreshWidgetPositions(ci: CallbackInfo) {
        if (addResourcePackButton == null) return
        addResourcePackButton!!.setPosition(width / 2 - 215, height - 49)
    }

    @ModifyArg(method = ["init"], at = At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/packs/TransferableSelectionList;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/packs/PackSelectionScreen;IILnet/minecraft/network/chat/Component;)V", ordinal = 0), index = 3)
    fun capyclientModifyPackListWidgetTitle(par3: Int): Int {
        return if (!PackAdder.isEnabled()) par3 else par3 - 20
    }
}