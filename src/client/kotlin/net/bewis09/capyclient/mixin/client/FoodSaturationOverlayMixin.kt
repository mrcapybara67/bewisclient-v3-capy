// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.ColorSaturation
import net.bewis09.capyclient.version.GuiGraphics
import net.bewis09.capyclient.version.Hud
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

/**
 * Mixes into [Hud] to render an AppleSkin-style food saturation
 * overlay on the hunger bar.
 *
 * Features:
 * 1. **Saturation overlay** — a semi-transparent golden bar behind
 *    the regular hunger bar that shows the player's current saturation.
 * 2. **Food preview** — when holding a food item, translucent shanks
 *    are drawn beyond the current hunger level showing how much hunger
 *    would be restored by eating the held food.
 *
 * The food bar in vanilla Minecraft is rendered at:
 *   x = screenWidth / 2 + 91   (right side)
 *   y = screenHeight - 39      (bottom, above hotbar)
 * Each chicken leg is 8×8 px with 1 px gap between legs.
 */
@Mixin(Hud::class)
abstract class FoodSaturationOverlayMixin {

    /**
     * Inject AFTER the food level is rendered to draw the saturation
     * overlay and food preview on top.
     *
     * @[1.21.8] "renderFoodLevel" @[] "renderFoodLevel"
     */
    @Inject(method = [/*[@]*/"renderFoodLevel"/*[!@]*/], at = [At("RETURN")])
    private fun onPostRenderFoodLevel(guiGraphics: GuiGraphics, ci: CallbackInfo) {
        if (!ColorSaturation.isEnabled()) return

        val player = Minecraft.getInstance().player ?: return
        val foodData = player.foodData
        val currentFood = foodData.foodLevel
        val currentSaturation = foodData.saturationLevel

        val screenWidth = Minecraft.getInstance().window.guiScaledWidth
        val screenHeight = Minecraft.getInstance().window.guiScaledHeight

        // Food bar position (vanilla: right side, above hotbar)
        val startX = screenWidth / 2 + 91
        val startY = screenHeight - 39

        // Each food shank = 8 px wide, 1 px gap between shanks
        val shankWidth = 8
        val shankGap = 1
        val totalShankStep = shankWidth + shankGap

        // ---- 1. Saturation overlay ----
        if (ColorSaturation.showSaturationBar.get() && currentSaturation > 0.0f) {
            val saturationShanks = (currentSaturation / 2.0f).coerceAtMost(10.0f)

            // Draw golden saturation bars behind the food bar
            for (i in 0 until saturationShanks.toInt()) {
                val shankX = startX - i * totalShankStep - shankWidth
                if (shankX < 0) break

                val isFullySaturated = (i * 2 + 2) <= currentSaturation.toInt()
                val fillColor = if (isFullySaturated) 0x44FFFFAA else 0x33FFD700

                guiGraphics.fill(shankX, startY, shankX + shankWidth, startY + 8, fillColor)
            }
        }

        // ---- 2. Food preview (restoration preview) ----
        if (ColorSaturation.showFoodPreview.get()) {
            val heldFood = getHeldEdible()
            val foodProps = heldFood?.item?.foodProperties ?: return

            val nutrition = foodProps.nutrition
            if (nutrition <= 0) return

            // Calculate how many additional half-shanks would be restored
            val previewShanks = (nutrition / 2.0f).toInt().coerceAtMost(10 - currentFood / 2)

            // Draw translucent red shanks for the food that would be restored
            for (i in 0 until previewShanks) {
                val shankIndex = currentFood / 2 + i
                if (shankIndex >= 10) break

                val shankX = startX - shankIndex * totalShankStep - shankWidth
                if (shankX < 0) break

                // Semi-transparent red/orange preview
                guiGraphics.fill(shankX, startY, shankX + shankWidth, startY + 8, 0x66FF5555)
            }
        }
    }

    @Unique
    private fun getHeldEdible(): ItemStack? {
        val player = Minecraft.getInstance().player ?: return null
        val mainHand = player.mainHandItem
        if (mainHand.isEdible) return mainHand
        val offHand = player.offhandItem
        if (offHand.isEdible) return offHand
        return null
    }
}
