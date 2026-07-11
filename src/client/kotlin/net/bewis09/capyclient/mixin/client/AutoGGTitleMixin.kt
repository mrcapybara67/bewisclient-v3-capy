package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.AutoGG
import net.bewis09.capyclient.version.Hud
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [Hud] (net.minecraft.client.gui.Gui) to detect match-end
 * title/subtitle overlays (e.g. "VICTORY!", "GAME OVER!") that servers
 * send as centered title text — this is the primary way vanilla servers
 * indicate a match has ended, and it works generically across any server
 * that sends a standard title packet, not just Hypixel.
 *
 * Both [AutoGG.sendChatLine] paths (title and subtitle) share the same
 * cooldown and fireOnMatchEnd gate, so duplicate events for the same
 * match end cannot double-fire.
 *
 * In 1.21.11 Mojang mappings, [Hud] (Gui) has:
 *   setTitle(Component)
 *   setSubtitle(Component)
 * The subtitle packet is typically sent BEFORE the title packet, so
 * injecting at both independently covers whichever one carries the
 * match-end keyword.
 */
@Mixin(Hud::class)
abstract class AutoGGTitleMixin {

    @Inject(method = ["setTitle"], at = [At("HEAD")])
    private fun capyclientOnSetTitle(title: Component, ci: CallbackInfo) {
        AutoGG.handleTitleMatch(title)
    }

    @Inject(method = ["setSubtitle"], at = [At("HEAD")])
    private fun capyclientOnSetSubtitle(subtitle: Component, ci: CallbackInfo) {
        AutoGG.handleTitleMatch(subtitle)
    }
}
