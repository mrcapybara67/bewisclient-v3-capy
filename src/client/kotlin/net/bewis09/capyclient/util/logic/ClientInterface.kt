package net.bewis09.capyclient.util.logic

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.common.logic.BewisclientLogger
import net.bewis09.capyclient.common.logic.FileLogic
import net.bewis09.capyclient.common.logic.WebLogic
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.minecraft.RenderableScreen
import net.bewis09.capyclient.version.getScreen
import net.bewis09.capyclient.version.setScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.server.packs.resources.Resource
import net.minecraft.sounds.SoundEvents
import java.util.function.Predicate

/**
 * Interface for the Bewisclient which should be implemented by most classes in the Bewisclient codebase to access important utilities more easily.
 */
interface ClientInterface : BewisclientLogger, FileLogic, InGameLogic, DrawingLogic, WebLogic {
    // The safe call `?.` is preserved even though the Kotlin compiler flags it as
    // unnecessary: under Loom's runDatagen runtime, Minecraft.getInstance() is
    // constructed without a real Window, so client.window can be null at runtime
    // even though Yarn's Window return type is non-nullable. The `?: 1000` also
    // covers the rare case where the window is 0×0 during early init.
    @Suppress("UNNECESSARY_SAFE_CALL")
    val screenWidth
        get() = client.window?.guiScaledWidth ?: 1000

    @Suppress("UNNECESSARY_SAFE_CALL")
    val screenHeight
        get() = client.window?.guiScaledHeight ?: 1000

    val client: Minecraft
        get() = Minecraft.getInstance()

    fun playClickSound() {
        client.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }

    val scaleFactor
        get() = client.window.guiScale.toFloat().toInt()

    fun isInWorld() = client.level != null

    fun getCurrentRenderableScreen() = (getScreen() as? RenderableScreen)

    fun setRenderableScreen(screen: Renderable) = setScreen(RenderableScreen(screen))

    fun findAllResources(path: String, filter: Predicate<Identifier>): Map<Identifier, List<Resource>> {
        return client.resourceManager.listResourceStacks(path) { filter.test(it) }.mapKeys { it.key }
    }
}