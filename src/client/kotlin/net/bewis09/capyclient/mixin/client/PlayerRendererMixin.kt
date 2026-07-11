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
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(LivingEntityRenderer::class)
abstract class PlayerRendererMixin {

    /**
     * Cache key for the local player's styled nametag.
     * Rebuilding the component every frame causes unnecessary allocations;
     * we only rebuild when one of these inputs changes.
     */
    @Unique
    private data class CapyNametagCacheKey(
        val colorInt: Int,
        val bold: Boolean,
        val italic: Boolean,
        val prefix: String,
        val baseName: String,
        val suffix: String,
        val health: Int?,
        val ping: Int?
    )

    @Unique
    private var capyLastNametagKey: CapyNametagCacheKey? = null

    @Unique
    private var capyCachedNametag: Component? = null

    @Unique
    private var capyLastBbHeight: Float = -1f

    @Unique
    private var capyCachedAttachment: Vec3? = null
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
        val bold = PlayerNametag.bold.get()
        val italic = PlayerNametag.italic.get()
        val prefix = PlayerNametag.prefix.get()
        val baseName = entity.name.string
        val suffix = PlayerNametag.suffix.get()

        val colorInt: Int = if (PlayerNametag.rainbow.get()) {
            // Quantise time so the rainbow colour only updates every 50ms,
            // allowing the nametag cache to hit instead of rebuilding every frame.
            val quantized = (System.currentTimeMillis() / 50) * 50
            val hue = (quantized / 1000f * PlayerNametag.rainbowSpeed.get()) % 1f
            java.awt.Color.HSBtoRGB(hue, 1f, 1f)
        } else {
            PlayerNametag.color.get().getColorInt()
        }

        val health = if (PlayerNametag.showHealth.get()) entity.health.roundToInt() else null
        val ping = if (PlayerNametag.showPing.get()) {
            mc.connection?.getPlayerInfo(entity.uuid)?.latency ?: 0
        } else null

        val cacheKey = CapyNametagCacheKey(colorInt, bold, italic, prefix, baseName, suffix, health, ping)

        val tag = if (capyLastNametagKey == cacheKey) {
            capyCachedNametag
        } else {
            val nameComp = Component.literal(prefix + baseName + suffix)
                .withStyle { style -> style.withColor(colorInt).withBold(bold).withItalic(italic) }

            val newTag: net.minecraft.network.chat.MutableComponent = nameComp

            if (health != null) {
                newTag.append(
                    Component.literal(" ${health}❤")
                        .withStyle { style -> style.withColor(0xFF5555).withBold(bold).withItalic(italic) }
                )
            }

            if (ping != null) {
                newTag.append(
                    Component.literal(" ${ping}ms")
                        .withStyle { style -> style.withColor(0xFFFF55).withBold(bold).withItalic(italic) }
                )
            }

            capyLastNametagKey = cacheKey
            capyCachedNametag = newTag
            newTag
        }

        state.nameTag = tag

        // Cache the attachment vector; only rebuild when bbHeight changes.
        val bbHeight = entity.getBbHeight()
        val attachment = if (capyLastBbHeight == bbHeight) {
            capyCachedAttachment
        } else {
            capyLastBbHeight = bbHeight
            Vec3(0.0, bbHeight.toDouble() + 0.5, 0.0).also { capyCachedAttachment = it }
        }
        state.nameTagAttachment = attachment
    }
}
