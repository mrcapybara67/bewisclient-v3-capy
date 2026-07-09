package net.bewis09.bewisclient.features.utilities

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.settings.structure.ImageFeature

/**
 * Caps the size of the vanilla chat history queue to avoid the per-frame
 * micro-stutters that occur when large multiplayer servers flood the chat
 * (some servers dump thousands of join/leave/kill messages per tick).
 *
 * The vanilla Minecraft chat log is iterated and re-laid-out on every frame
 * to render the visible portion of the chat. When the queue grows into the
 * thousands the per-frame cost grows linearly, producing noticeable
 * staggered frame times. By dropping new incoming messages once a hard cap
 * is reached we keep the queue at a fixed size and the per-frame cost flat.
 *
 * Anti-stutter-only behaviour: when the cap is reached, NEW messages are
 * silently dropped (not queued). The existing visible chat is preserved so
 * the user keeps seeing recent history rather than letting the queue
 * continue blowing up.
 */
object NoChatLag : ImageFeature(createIdentifier("bewisclient", "no_chat_lag"), "No Chat Lag") {
    val maxMessages = int("max_messages", 200, 10, 10000)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(this, maxMessages, "max_messages",
            "Max Chat Messages",
            "Hard cap on the number of chat messages kept in history. New messages beyond this cap are dropped. Lower values use less memory and avoid frame spikes on big servers.",
            "max_messages"
        )
    }
}
