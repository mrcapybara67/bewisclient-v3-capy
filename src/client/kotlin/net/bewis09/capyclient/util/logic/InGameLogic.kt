// @VersionReplacement

package net.bewis09.capyclient.util.logic

import net.bewis09.capyclient.util.Bewisclient
import net.minecraft.network.chat.Component

interface InGameLogic {
    fun showTitle(message: Component) {
        // @[1.21.11] displayClientMessage(message, true) @[] sendOverlayMessage(message)
        Bewisclient.client.player?./*[@]*/displayClientMessage(message, true)/*[!@]*/
    }

    fun showSystemMessage(message: Component) {
        // @[1.21.11] displayClientMessage(message, false) @[] sendSystemMessage(message)
        Bewisclient.client.player?./*[@]*/displayClientMessage(message, false)/*[!@]*/
    }
}