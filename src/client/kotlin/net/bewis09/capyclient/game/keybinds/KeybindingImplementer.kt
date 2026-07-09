package net.bewis09.capyclient.game.keybinds

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.features.utilities.Perspective
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.version.createCategory
import net.bewis09.capyclient.version.registerKeyBinding
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

object KeybindingImplementer : EventEntrypoint {
    init {
        Translation("key.category.capyclient", "category", "Capy Client")
    }

    val category = createCategory(createIdentifier("capyclient", "category"))

    override fun onInitializeClient() {
        val keybinds = APIEntrypointLoader.mapEntrypoint { it.getKeybinds() }.flatten()

        keybinds.map(Keybind::keyBinding).forEach(::registerKeyBinding)

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            keybinds.forEach {
                while (it.keyBinding.consumeClick()) {
                    it.action?.invoke()
                }

                it.tick?.let { a ->
                    a(it.keyBinding.isDown)
                }
            }

            if (client.options.keyTogglePerspective.isDown) {
                Perspective.cameraAddPitch = 0f
                Perspective.cameraAddYaw = 0f
            }
        })
    }
}