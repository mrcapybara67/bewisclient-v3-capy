// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.features.utilities.FoodSaturationOverlay
import net.bewis09.capyclient.version.GuiGraphics
import net.bewis09.capyclient.version.Hud
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.world.food.FoodProperties
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
 * 1. **Saturation overlay** — golden bars drawn ON TOP of the
 *    existing hunger bar to show the player's current saturation.
 *    Each saturated half-shank is filled with gold.
 * 2. **Food preview** — when holding a food item, translucent
 *    shanks are drawn beyond the current hunger level showing
 *    how much hunger would be restored by eating the held food.
 *
 * The vanilla food bar renders at:
 *   startX = screenWidth / 2 + 91  (rightmost shank)
 *   y = screenHeight - 39          (bottom, above hotbar)
 * Each food shank is 8×8 px with 1 px gap (9 px step).
 * The bar goes RIGHT-TO-LEFT: index 0 = rightmost (fullest).
 */
@Mixin(Hud::class)
abstract class FoodSaturationOverlayMixin {

    @Unique
    private fun mc(): Minecraft = Minecraft.getInstance()

    /**
     * Inject AFTER the food level is rendered to draw the saturation
     * overlay and food preview.
     *
     * @[1.21.8] "renderFoodLevel" @[] "renderFood"
     */
    @Inject(method = [/*[@]*/"renderFoodLevel"/*[!@]*/], at = [At("RETURN")])
    private fun onPostRenderFoodLevel(guiGraphics: GuiGraphics, ci: CallbackInfo) {
        if (!FoodSaturationOverlay.isEnabled()) return

        val player = mc().player ?: return
        val foodData = player.foodData
        val currentFood = foodData.foodLevel.toInt()
        val currentSaturation = foodData.saturationLevel.toFloat()

        val screenWidth = mc().window.guiScaledWidth.toInt()
        val screenHeight = mc().window.guiScaledHeight.toInt()

        // Food bar position (vanilla)
        val startX = screenWidth / 2 + 91
        val startY = screenHeight - 39
        val shankW = 8
        val step = 9  // shankW + 1px gap

        // ---- 1. Saturation overlay (golden bars on existing hunger) ----
        if (FoodSaturationOverlay.showSaturationBar.get() && currentSaturation > 0.0f) {
            // Saturation is in half-shanks (0-20). Convert to shank index (0-10).
            val shanks = (currentSaturation / 2.0f).coerceAtMost(10f).toInt()
            if (shanks > 0) {
                // Draw a bright gold bar over the saturated portion of the food bar
                for (i in 0 until shanks) {
                    val sx = (startX - i * step - shankW).toInt()
                    if (sx < 0) break

                    val isFull = (i * 2 + 2) <= currentSaturation.toInt().coerceAtMost(20)
                    // Use bright, visible colors: AARRGGBB
                    val color = if (isFull) 0xBBFFD700.toInt()  // Bright gold, 73% alpha
                                else 0x88B8860B.toInt()         // Dark gold, 53% alpha

                    guiGraphics.fill(sx, startY, sx + shankW, startY + 8, color)
                }
            }
        }

        // ---- 2. Food preview (restoration preview when holding food) ----
        if (FoodSaturationOverlay.showFoodPreview.get()) {
            val heldFood = getHeldEdible()
            val foodProps: FoodProperties = heldFood?.get(DataComponents.FOOD) ?: return

            val nutrition = foodProps.nutrition().toInt()
            if (nutrition <= 0) return

            // Calculate how many additional shanks would be restored
            val currentFullShanks = currentFood / 2
            val previewHalfShanks = nutrition.coerceAtMost(20 - currentFood)
            val previewFullShanks = previewHalfShanks / 2
            val showHalfShank = previewHalfShanks % 2 == 1

            // Draw translucent orange/red shanks beyond current food level
            for (i in 0 until previewFullShanks) {
                val shankIdx = currentFullShanks + i
                if (shankIdx >= 10) break

                val sx = (startX - shankIdx * step - shankW).toInt()
                if (sx < 0) break

                // Visible orange-red: A=0x99(60%), R=FF, G=55, B=00
                guiGraphics.fill(sx, startY, sx + shankW, startY + 8, 0x99FF5500.toInt())
            }

            // Draw half-shank indicator if odd nutrition
            if (showHalfShank && currentFullShanks + previewFullShanks < 10) {
                val shankIdx = currentFullShanks + previewFullShanks
                val sx = (startX - shankIdx * step - shankW).toInt()
                if (sx >= 0) {
                    // Half-width bar: draw only the left half of the shank
                    guiGraphics.fill(sx, startY, sx + shankW / 2, startY + 8, 0x99FF5500.toInt())
                }
            }
        }
    }

    @Unique
    private fun getHeldEdible(): ItemStack? {
        val player = mc().player ?: return null
        val mainHand = player.mainHandItem
        if (mainHand.has(DataComponents.FOOD)) return mainHand
        val offHand = player.offhandItem
        if (offHand.has(DataComponents.FOOD)) return offHand
        return null
    }
}