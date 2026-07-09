package net.bewis09.capyclient.game

import com.mojang.brigadier.arguments.StringArgumentType
import net.bewis09.capyclient.common.toText
import net.bewis09.capyclient.features.sidebar.Screenshot
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.version.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import java.io.File

object BewisclientCommand : EventEntrypoint {
    override fun onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("capyclient").then(
                    ClientCommandManager.literal("screenshot").then(
                        ClientCommandManager.argument("path", StringArgumentType.greedyString()).executes {
                            val path = StringArgumentType.getString(it, "path")
                            it.source.client.execute {
                                Screenshot.openBigScreenshotNewScreen(File(path))
                            }
                            1
                        }
                    )
                ).executes {
                    it.source.sendFeedback("Capy Client v${FabricLoader.getInstance().allMods.firstOrNull { a -> a.metadata.id == "capyclient" }?.metadata?.version ?: "Unknown Version"}".toText())
                    1
                })
        }
    }
}