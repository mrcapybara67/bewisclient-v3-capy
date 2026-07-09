package net.bewis09.bewisclient.mixin.client

import net.bewis09.bewisclient.features.utilities.PlayerNametag
import net.minecraft.client.Minecraft
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import kotlin.math.roundToInt
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(LivingEntityRenderer::class)
abstract class PlayerRendererMixin {
    /**
     * Per-tick marker so we only ever set the local player's nametag once
     * per tick. In third-person F5 the world is rendered twice (back and
     * mirror-front); vanilla would then submit the same `state.nameTag` in
     * both render jobs and end up drawing two nametags over the local
     * player. Recording the last tick we wrote a tag in and skipping the
     * second write closes that loophole without changing any other
     * behaviour.
     */
    @Volatile
    private var lastLocalNametagTick: Long = -1L

    /**
     * Hooks AFTER LivingEntityRenderer has finished building the render state for
     * the current tick. The previous build targeted EntityRenderer.extractRenderState,
     * which — due to JVM type erasure — matched the bridge method with
     * `EntityRenderState` instead of the actual `LivingEntityRenderState`. The
     * mixin therefore silently failed to inject. Mixing on the concrete
     * LivingEntityRenderer's exact erased signature makes the @RETURN injection
     * the last write before submit.
     *
     * Filters to AbstractClientPlayer so mobs/critters are untouched, optionally
     * gates the local player by camera type (first-person hides the tag because it
     * overlaps the HUD; third-person / F5 shows the tag at the player's chosen
     * color), and gates by squared distance. Color logic supports both the
     * static color setting and a rainbow hue set by HSBtoRGB driven by wallclock
     * time × rainbowSpeed.
     */
    @Inject(method = ["extractRenderState"], at = [At("RETURN")])
    fun bewisclientPlayernametag(entity: LivingEntity, state: LivingEntityRenderState, f: Float, ci: CallbackInfo) {
        if (!PlayerNametag.isEnabled()) {
            // Feature disabled: don't touch the vanilla tag so F5 / first-person
            // render exactly as before, without any ghost duplication.
            return
        }
        if (entity !is AbstractClientPlayer) return

        // Hoist Minecraft.getInstance() to a single local — this hot path runs once per
        // LivingEntity per frame; the unhoisted version called it 3 times per match.
        val mc = Minecraft.getInstance()
        val isLocalPlayer = entity === mc.player

        // Local player handling:
        //  - self_nametag off: skip local entirely
        //  - first-person camera: always skip (HUD clipping)
        //  - self_mode: "always" → keep showing in F5, "third_person" → only when not first-person, "never" → never
        val cameraType = mc.options.cameraType
        if (isLocalPlayer) {
            if (!PlayerNametag.selfNametag.get()) return
            val isFirstPerson = cameraType.isFirstPerson
            when (PlayerNametag.selfNametagMode.get().lowercase()) {
                "never" -> return
                "third_person" -> if (isFirstPerson) return
                "always" -> { /* always render */ }
                else -> { /* unknown value → fall back to third-person default */ if (isFirstPerson) return }
            }
        }

        val maxDist = PlayerNametag.visibleDistance.get()
        if (mc.entityRenderDispatcher.distanceToSqr(entity) > (maxDist * maxDist).toDouble()) {
            // Out of range: explicitly clear the tag so vanilla doesn't render a
            // stale (e.g., un-colored) name from a previous state's leftover.
            // Only the local player needs explicit clearing; vanilla already
            // gates other players by their own visibility distance.
            if (isLocalPlayer) {
                state.nameTag = null
            }
            return
        }

        // Local-player F5-mirror guard: in third-person, the world is
        // submitted twice (back + mirror front) on the same tick, each
        // calling extractRenderState for the local player. Without this
        // guard both submits would carry the same `state.nameTag` and
        // both would render — producing the visible "two names" bug. We
        // detect the second pass with a tick id and clear the tag.
        if (isLocalPlayer) {
            val currentTick = mc.level?.gameTime ?: 0L
            if (currentTick == lastLocalNametagTick) {
                state.nameTag = null
                return
            }
            lastLocalNametagTick = currentTick
        }

        val colorInt: Int = if (PlayerNametag.rainbow.get()) {
            val hue = (System.currentTimeMillis() / 1000f * PlayerNametag.rainbowSpeed.get()) % 1f
            java.awt.Color.HSBtoRGB(hue, 1f, 1f)
        } else {
            PlayerNametag.color.get().getColorInt()
        }

        // Build the text payload: prefix + name + optional suffix + optional
        // health/ping suffix. showHealth/ping are co-rendered inline as plain
        // digits rather than a separate widget because LivingEntityRenderState
        // exposes only `nameTag` for text. The digits inherit the configured
        // color which keeps the look consistent.
        val baseName = entity.name.string
        // `entity` is already typed `LivingEntity` from the mixin signature —
        // no `is` check needed; just read `entity.health` directly.
        val healthText = if (PlayerNametag.showHealth.get()) {
            // roundToInt() instead of toInt() so 19.5 HP displays as 20 (matching
            // the visual heart count shown by the vanilla HUD) rather than 19.
            " §c${entity.health.roundToInt()}❤"
        } else ""
        // For ping we look up the targeted entity's PlayerInfo via the
        // client-side ClientPacketListener. The `Player.connection` field is a
        // server-side concept; in 1.21.11 it does NOT exist on
        // AbstractClientPlayer, so we must go through the network connection
        // that tracks every player's tab-listed ping. `latency` is a field
        // (Int) on PlayerInfo. Safe-calls cover the brief null windows during
        // login / world change / entity despawn.
        val pingText = if (PlayerNametag.showPing.get()) {
            val ping = mc.connection?.getPlayerInfo(entity.uuid)?.latency ?: 0
            " §e${ping}ms"
        } else ""
        val body = PlayerNametag.prefix.get() + baseName + healthText + pingText + PlayerNametag.suffix.get()

        // `Style` is immutable in vanilla — `with*` returns a new instance, so we
        // chain the calls rather than wrapping in `also {}` (which would discard
        // the new instances and silently drop bold/italic). The inline lambda
        // body returns the final Style which is what the builder accepts.
        val bold = PlayerNametag.bold.get()
        val italic = PlayerNametag.italic.get()
        state.nameTag = Component.literal(body).withStyle { style ->
            style.withColor(colorInt).withBold(bold).withItalic(italic)
        }
        // Vanilla EntityRenderer uses entity.getBbHeight() + 0.5 for the nametag-attachment
        // vertical offset, which gives the tag breathing room above the head. The previous
        // build used + 0.2, causing the rendered tag to clip into the player's head/helmet
        // and look broken/ugly. Restore vanilla-accurate + 0.5.
        state.nameTagAttachment = Vec3(0.0, entity.getBbHeight().toDouble() + 0.5, 0.0)
    }
}
