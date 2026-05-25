package net.bewis09.bewisclient.util.logic

import net.bewis09.bewisclient.common.logic.BewisclientLogger
import net.bewis09.bewisclient.common.logic.FileLogic
import net.bewis09.bewisclient.common.logic.WebLogic
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.sounds.SoundEvents

/**
 * Interface for the Bewisclient which should be implemented by most classes in the Bewisclient codebase to access important utilities more easily.
 */
interface ClientInterface : BewisclientLogger, FileLogic, InGameLogic, DrawingLogic, WebLogic {
    val screenWidth
        get() = client.window?.guiScaledWidth ?: 1000

    val screenHeight
        get() = client.window?.guiScaledHeight ?: 1000

    val util: UtilLogic
        get() = UtilLogic

    val client: Minecraft
        get() = Minecraft.getInstance()

    fun playClickSound() {
        client.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    }
}