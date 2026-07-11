package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.PlayerNametag
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
     * Injects AFTER [LivingEntityRenderer.extractRenderState] to customise
     * the nametag that appears above each player.
     *
     * The method has two logical halves:
     *   1. **Self-nametag mode** (isLocalPlayer only) — the selfNametag
     *      toggle and the always/third_person/never mode selector control
     *      whether the local player's own name is shown in third-person.
     *   2. **Rich styling** (ALL players) — color/rainbow, prefix, suffix,
     *      showHealth, showPing, bold, and italic are applied to every
     *      player within visibleDistance.
     *
     * When the feature is globally disabled, other players' nametags are
     * hidden and the local player's tag falls back to vanilla rendering.
     */
    @Inject(method = ["extractRenderState"], at = [At("RETURN")])
    fun capyclientPlayernametag(entity: LivingEntity, state: LivingEntityRenderState, f: Float, ci: CallbackInfo) {
        if (entity !is AbstractClientPlayer) return

        val mc = Minecraft.getInstance()
        val isLocalPlayer = entity === mc.player

        // Feature off: leave every nametag untouched (vanilla handles it).
        if (!PlayerNametag.isEnabled()) return

        // ==============================================================
        //  Visible-distance check applies before the self/other split.
        // ==============================================================
        val maxDist = PlayerNametag.visibleDistance.get()
        if (mc.entityRenderDispatcher.distanceToSqr(entity) > (maxDist * maxDist).toDouble()) return

        // ==============================================================
        //  Other players: never touch their nametag state. Vanilla will
        //  render their normal white username above their head.
        // ==============================================================
        if (!isLocalPlayer) return

        // ==============================================================
        //  Self-nametag mode: only applies to the local player's own tag.
        //  (always / third_person / never + the selfNametag toggle)
        // ==============================================================
        if (!PlayerNametag.selfNametag.get()) {
            state.nameTag = null
            return
        }

        val cameraType = mc.options.cameraType
        val isFirstPerson = cameraType.isFirstPerson
        when (PlayerNametag.selfNametagMode.get().lowercase()) {
            "never" -> { state.nameTag = null; return }
            "third_person" -> if (isFirstPerson) { state.nameTag = null; return }
            "always" -> { /* always render */ }
            else -> if (isFirstPerson) { state.nameTag = null; return }
        }

        // ==============================================================
        //  Build the styled nametag for the local player only.
        // ==============================================================
        val colorInt: Int = if (PlayerNametag.rainbow.get()) {
            val hue = (System.currentTimeMillis() / 1000f * PlayerNametag.rainbowSpeed.get()) % 1f
            java.awt.Color.HSBtoRGB(hue, 1f, 1f)
        } else {
            PlayerNametag.color.get().getColorInt()
        }

        val bold = PlayerNametag.bold.get()
        val italic = PlayerNametag.italic.get()

        val prefix = PlayerNametag.prefix.get()
        val baseName = entity.name.string
        val suffix = PlayerNametag.suffix.get()

        // Start with the name (coloured + bold/italic).  Use
        // MutableComponent directly so we can .append() without
        // unnecessary .copy() calls.
        val nameComp = Component.literal(prefix + baseName + suffix)
            .withStyle { style -> style.withColor(colorInt).withBold(bold).withItalic(italic) }

        // Cast to MutableComponent for efficient append() — in
        // practice withStyle() always returns a MutableComponent.
        val tag: net.minecraft.network.chat.MutableComponent = nameComp

        // Append health text with red colour (NO § codes)
        if (PlayerNametag.showHealth.get()) {
            tag.append(
                Component.literal(" ${entity.health.roundToInt()}❤")
                    .withStyle { style -> style.withColor(0xFF5555).withBold(bold).withItalic(italic) }
            )
        }

        // Append ping text with yellow colour (NO § codes)
        if (PlayerNametag.showPing.get()) {
            val ping = mc.connection?.getPlayerInfo(entity.uuid)?.latency ?: 0
            tag.append(
                Component.literal(" ${ping}ms")
                    .withStyle { style -> style.withColor(0xFFFF55).withBold(bold).withItalic(italic) }
            )
        }

        state.nameTag = tag
        state.nameTagAttachment = Vec3(0.0, entity.getBbHeight().toDouble() + 0.5, 0.0)
    }
}
