package net.bewis09.bewisclient.game.keybinds

import net.bewis09.bewisclient.api.APIEntrypointLoader
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.features.utilities.Perspective
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.util.EventEntrypoint
import net.bewis09.bewisclient.version.createCategory
import net.bewis09.bewisclient.version.registerKeyBinding
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

object KeybindingImplementer : EventEntrypoint {
    init {
        Translation("key.category.bewisclient", "category", "Capy Client")
    }

    val category = createCategory(createIdentifier("bewisclient", "category"))

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