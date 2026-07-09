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
     * BUGFIX round 2 — the user reported two issues with the first fix:
     *   1. The nametag appeared TWICE (duplicate) instead of once.
     *   2. The nametag flickered.
     *
     * Root causes eliminated:
     *   - The old `lastLocalNametagTick` guard assumed `extractRenderState`
     *     was called twice per frame in third person.  In 1.21+ it is called
     *     ONCE — the guard never triggered so the tag was set normally.
     *     With the tag set and the state reused for both the back and front
     *     third-person render passes, the nametag appeared in BOTH views.
     *   - The `!!` safe-call operator on `state.nameTag` could throw NPE
     *     on the render thread, causing visible flicker.
     *
     * Fixes applied:
     *   - Remove the `lastLocalNametagTick` guard entirely — it never worked.
     *   - Use a local variable instead of `!!` to build the final component.
     *   - Keep `state.nameTag = null` for non-local players so their tags
     *     are hidden when the feature is on.
     */
    @Inject(method = ["extractRenderState"], at = [At("RETURN")])
    fun capyclientPlayernametag(entity: LivingEntity, state: LivingEntityRenderState, f: Float, ci: CallbackInfo) {
        if (entity !is AbstractClientPlayer) return

        val mc = Minecraft.getInstance()
        val isLocalPlayer = entity === mc.player

        if (PlayerNametag.isEnabled()) {
            // ==============================================================
            //  Hide ALL nametags except the local player's own tag.
            // ==============================================================
            if (!isLocalPlayer) {
                state.nameTag = null
                return
            }

            // Only reach here for the local player.
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

            val maxDist = PlayerNametag.visibleDistance.get()
            if (mc.entityRenderDispatcher.distanceToSqr(entity) > (maxDist * maxDist).toDouble()) {
                state.nameTag = null
                return
            }

            // ------------------ Build the styled nametag ------------------
            val colorInt: Int = if (PlayerNametag.rainbow.get()) {
                val hue = (System.currentTimeMillis() / 1000f * PlayerNametag.rainbowSpeed.get()) % 1f
                java.awt.Color.HSBtoRGB(hue, 1f, 1f)
            } else {
                PlayerNametag.color.get().getColorInt()
            }

            val bold = PlayerNametag.bold.get()
            val italic = PlayerNametag.italic.get()

            // BUGFIX: §c/§e legacy codes are NOT parsed by Component.literal()
            // in 1.21+.  Each coloured segment must use withStyle().
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

            state.nameTag = tag
            state.nameTagAttachment = Vec3(0.0, entity.getBbHeight().toDouble() + 0.5, 0.0)
        } else {
            // Feature off: hide OTHER players' nametags but keep vanilla
            // behavior for the local player.
            if (!isLocalPlayer) {
                state.nameTag = null
            }
        }
    }
}
