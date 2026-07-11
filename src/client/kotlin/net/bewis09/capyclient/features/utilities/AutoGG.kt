package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature
import net.bewis09.capyclient.util.EventEntrypoint
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import org.slf4j.LoggerFactory

object AutoGG : ImageFeature(createIdentifier("capyclient", "auto_gg"), "AutoGG"), EventEntrypoint {
    private val log = LoggerFactory.getLogger("CapyAutoGG")

    val preset = string("preset", "gg")
    val useCustom = boolean("use_custom", false)
    val custom = string("custom", "GG!")
    // Cooldown (ms) so a single kill/death/match-end doesn't fire multiple chat lines
    // in a burst (e.g., if the server sends the kill message a second time for chat
    // signing). Without it a quick "double-fire" could land two "gg" messages in chat.
    val cooldownMs = int("cooldown_ms", 100, 0, 10000)
    // Fire on match-end style messages (you won, victory!, ...). Off by default
    // because a casual "gg!" in chat would also match — opt-in for ranked/lobby
    // servers where the chat is moderated.
    val fireOnMatchEnd = boolean("fire_on_match_end", true)

    @Volatile
    private var lastSentAt: Long = 0L

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, preset, "preset",
            "Preset Message",
            "One of the preset messages sent after a kill. Common presets: 'gg', 'ggs', 'nice match'. " +
                "Supports comma-separated lists if you want multiple messages rotated.",
            "preset",
        )
        list.addRenderable(
            this, useCustom, "use_custom",
            "Use Custom Message",
            "When on, AutoGG sends your custom message below instead of the preset. " +
                "Pure chat etiquette — no gameplay automation.",
            "use_custom",
        )
        list.addRenderable(
            this, custom, "custom",
            "Custom Message",
            "The custom AutoGG message. Only used when 'Use Custom Message' is enabled.",
            "custom",
        )
        list.addRenderable(this, fireOnMatchEnd, "match_end",
            "Also fire on match end",
            "If on, AutoGG will also send the message on clear match-end lines such as 'You won the match!', 'Victory!' or 'Game Over!'.",
            "match_end"
        )
        list.addRenderable(this, cooldownMs, "cooldown_ms",
            "Cooldown (ms)",
            "Minimum gap between two AutoGG chat sends to avoid double-firing on duplicate events.",
            "cooldown_ms"
        )
    }

    override fun onInitializeClient() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
            handlePotentialDeathMessage(message)
            true
        }
    }

    /**
     * Triggered by Fabric's [ClientReceiveMessageEvents.ALLOW_GAME] whenever a
     * game chat / system chat / death message arrives at the client. Fires
     * [onLocalPlayerKill] on three distinct events:
     *
     * 1. **Vanilla kill** — `death.attack.*` translatable message with the local
     *    player as the killer.
     * 2. **Plugin-style kill / PvP log line** — server uses plain text without a
     *    translatable component (e.g., `Steve was slain by Alex`). We detect
     *    "<local name> ... by ..." and similar shapes.
     * 3. **Match-end** — opt-in chat lines like `You won the match!`, `Victory!`,
     *    `Game Over!`, `Match ended`. Disabled when [fireOnMatchEnd] is off to
     *    avoid false positives on autocompleted GG chat.
     *
     * Across all three paths, the same cooldown-and-rotation logic in
     * [sendChatLine] is used so behaviour stays consistent.
     */
    fun handlePotentialDeathMessage(message: Component) {
        val rawString = runCatching { message.string }.getOrNull() ?: "<unparseable>"
        // TEMPORARY DEBUG: log every incoming game message so we can see the
        // exact kill/death format used by mazerclub.net / minesive.com.
        log.info("[AutoGG] RAW_GAME_MESSAGE: {}", rawString)
        val isEn = isEnabled()
        log.info("[AutoGG] handlePotentialDeathMessage called: isEnabled={}, rawMessage={}", isEn, rawString)

        if (!isEn) {
            log.info("[AutoGG] handlePotentialDeathMessage: returning early because isEnabled()=false")
            return
        }
        val player = Minecraft.getInstance().player ?: return
        val name = player.name.string
        if (name.isBlank()) return

        log.info("[AutoGG] handlePotentialDeathMessage: player name='{}', proceeding to detection stages", name)

        // Stage 0: match-end (opt-in). Cheap substring check fires before the
        // longer arg-shape scan so end-of-match chats reply faster.
        if (fireOnMatchEnd.get() && isMatchEndMessage(message)) {
            log.info("[AutoGG] Stage 0 (match-end) DETECTED! Calling sendChatLine")
            sendChatLine()
            return
        }

        // Stage 1: vanilla death TranslatableContents with structured args.
        val contents = message.contents
        if (contents is TranslatableContents && contents.key.startsWith("death.attack.")) {
            val victim = contents.args.getOrNull(0)
            val killer = contents.args.getOrNull(1)
            val victimIsLocal = argMatchesName(victim, name)
            val killerIsLocal = argMatchesName(killer, name)
            log.info("[AutoGG] Stage 1 (vanilla kill): key={}, victim={}, killer={}, victimIsLocal={}, killerIsLocal={}",
                contents.key, victim, killer, victimIsLocal, killerIsLocal)
            if (!victimIsLocal && killerIsLocal) {
                log.info("[AutoGG] Stage 1 DETECTED kill! Calling sendChatLine")
                sendChatLine()
                return
            }
        }

        // Stage 2: legacy fallback — text-shape match (server-side plugin strings).
        val text = runCatching { message.string }.getOrNull() ?: return
        if (!text.contains(name)) {
            log.info("[AutoGG] Stage 2 (legacy): text='{}' does NOT contain name='{}', skipping", text, name)
            return
        }
        val byIdx = text.lastIndexOf(" by ")
        val nameIdx = text.lastIndexOf(name)
        log.info("[AutoGG] Stage 2 (legacy): text='{}', containsName=true, byIdx={}, nameIdx={}", text, byIdx, nameIdx)
        if (byIdx < 0 || nameIdx <= byIdx) return
        val trailing = text.substring(nameIdx + name.length)
        log.info("[AutoGG] Stage 2 (legacy): trailing='{}'", trailing)
        if (!trailing.all { it == ' ' || it == '.' || it == ',' || it == '!' || it == '?' || it == ')' || it == '*' || it == '\n' }) return
        log.info("[AutoGG] Stage 2 DETECTED kill! Calling sendChatLine")
        sendChatLine()
    }

    /**
     * Cheap textual match for the most common end-of-match chat lines. Carefully
     * scoped — we deliberately avoid matching bare 'gg' / 'ggs' / 'gg!' which
     * would also match someone else's casual chat that we shouldn't reply to.
     * Defeat-side lines ("you have been eliminated", "your team has been
     * eliminated") are intentionally excluded so we don't say "gg" when the
     * user just lost. The list is intentionally row-oriented (one phrase per
     * line) so a server admin can scan what we'll reply to.
     */
    private fun isMatchEndMessage(message: Component): Boolean {
        val raw = runCatching { message.string }.getOrNull() ?: return false
        val text = raw.lowercase()
        return text.contains("you won the match") ||
            text.contains("you won the game") ||
            text.contains("you have won") ||
            text.contains("victory royale") ||
            text.contains("game over!") ||
            text.contains("game over.") ||
            text.contains("match ended") ||
            text.contains("bedwars game ended") ||
            text.contains("your team won") ||
            text.contains("your team has won") ||
            text.contains("winner winner") ||
            text.contains("replay ended")
    }

    /**
     * Called from [AutoGGTitleMixin] when the server sends a centered
     * title overlay (e.g. "VICTORY!", "GAME OVER!"). Checks the title
     * text against the same keyword list used by [isMatchEndMessage]
     * and triggers [sendChatLine] if matched, respecting the
     * [fireOnMatchEnd] toggle and the cooldown.
     *
     * This is a second, more reliable match-end detection path that
     * works on ANY server sending a vanilla title packet — not just
     * servers with chat-based match-end messages.
     */
    fun handleTitleMatch(title: Component) {
        if (!isEnabled() || !fireOnMatchEnd.get()) return
        if (isMatchEndMessage(title)) {
            sendChatLine()
        }
    }

    private fun argMatchesName(arg: Any?, name: String): Boolean {
        return when (arg) {
            is Component -> arg.string.trim().equals(name, ignoreCase = false)
            is String -> arg.trim().equals(name, ignoreCase = false)
            else -> false
        }
    }

    /**
     * Sends the configured chat line, optionally picking the next one from a
     * comma-separated preset list. Capped by [cooldownMs] so two events firing
     * back-to-back (e.g., the kill message followed by a redundant fan-out) do
     * not spam chat.
     *
     * BUGFIX: the previous build captured `player.connection` at the outer
     * scope and then dispatched the send via `client.execute { ... }`. The
     * `execute` lambda runs on the next client tick, by which time the player
     * could have been replaced/closed (e.g., during a /logout or quick server
     * switch). Sending through a captured-but-closed connection throws an
     * exception on the main thread. We now re-fetch `client`/`player`/`connection`
     * inside the runnable and re-check the cooldown there so the entire
     * critical section stays on the main thread.
     */
    private fun sendChatLine() {
        log.info("[AutoGG] sendChatLine called!")
        val client = Minecraft.getInstance()
        val player = client.player ?: run { log.info("[AutoGG] sendChatLine: player is null, returning"); return }
        // Defensive: in older 1.21.x snapshots, connection could momentarily be
        // null during login/logout. The @Suppress is required because Kotlin
        // infers `player.connection` as non-null in 1.21.11+ (it isn't
        // formally annotated), which is exactly the case where the elvis is
        // truly useless — but we want the runtime guard anyway.
        @Suppress("USELESS_ELVIS")
        val earlyConnection = player.connection ?: run { log.info("[AutoGG] sendChatLine: earlyConnection is null, returning"); return }

        val raw = if (useCustom.get()) custom.get() else preset.get()
        val candidates = raw.split(',').map { it.trim() }.filter { it.isNotEmpty() }
        if (candidates.isEmpty()) {
            log.info("[AutoGG] sendChatLine: candidates empty (raw='{}'), returning", raw)
            return
        }

        log.info("[AutoGG] sendChatLine: raw='{}', candidates={}, cooldownMs={}", raw, candidates, cooldownMs.get())

        val cooldown = cooldownMs.get().toLong()

        // Cooldown and chat-send must run on the same thread, otherwise two
        // death messages processed back-to-back on different threads could
        // both pass the cooldown check and double-fire. Coalesce into the
        // client.execute block.
        client.execute(Runnable {
            val now = System.currentTimeMillis()
            if (cooldown > 0 && now - lastSentAt < cooldown) {
                log.info("[AutoGG] sendChatLine lambda: cooldown active (lastSent={}, now={}, cooldown={}), returning", lastSentAt, now, cooldown)
                return@Runnable
            }
            // Re-fetch on the main thread: the connection reference captured
            // above (`earlyConnection`) could be stale by the time this lambda
            // runs. Only proceed if the connection is still non-null.
            val live = Minecraft.getInstance().player?.connection
            if (live == null) {
                log.info("[AutoGG] sendChatLine lambda: live connection is null, returning")
                return@Runnable
            }
            lastSentAt = now
            val pick: String = if (useCustom.get()) candidates.first() else candidates[(now / 1000L % candidates.size).toInt()]
            log.info("[AutoGG] sendChatLine lambda: ABOUT TO SEND '{}' via live.sendChat()", pick)
            live.sendChat(pick)
            log.info("[AutoGG] sendChatLine lambda: live.sendChat() completed for '{}'", pick)
        })
    }
}
