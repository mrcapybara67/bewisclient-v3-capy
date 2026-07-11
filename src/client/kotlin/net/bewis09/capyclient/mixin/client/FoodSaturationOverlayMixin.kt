// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.features.utilities.FoodSaturationOverlay
import net.bewis09.capyclient.version.GuiGraphics
import net.bewis09.capyclient.version.Hud
import net.bewis09.capyclient.version.drawTexture
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.player.Player
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
 * 1. **Saturation overlay** — gold-tinted food icons drawn ON TOP
 *    of the existing hunger bar to show the player's current
 *    saturation. Reuses the same food icon texture from the GUI
 *    icons sheet so the overlay looks exactly like food shanks.
 * 2. **Food preview** — when holding a food item, orange-tinted
 *    food icons are drawn beyond the current hunger level showing
 *    how much hunger would be restored by eating the held food.
 *
 * Vanilla food bar layout (right-to-left):
 *   startX = screenWidth / 2 + 91   (rightmost shank)
 *   y = screenHeight - 39           (bottom, above hotbar)
 *   Each icon is 9×9 px with a 9 px step (right-to-left).
 */
@Mixin(Hud::class)
abstract class FoodSaturationOverlayMixin {

    companion object {
        /**
         * Minecraft's GUI icons texture — contains the food/shank
         * sprites used by the vanilla hunger bar.
         * Food icon positions on the 256×256 sheet:
         *   - Empty background: u=16,  v=27
         *   - Half food:        u=52,  v=27
         *   - Full food:        u=61,  v=27
         *   - Hardcore offset:  +36 to u
         */
        private val GUI_ICONS: Identifier = createIdentifier("minecraft", "textures/gui/icons.png")
    }

    @Unique
    private fun mc(): Minecraft = Minecraft.getInstance()

    /**
     * Inject AFTER the food level is rendered to draw the saturation
     * overlay and food preview.
     */
    // @[1.21.8] "renderFoodLevel" @[] "renderFood"
    @Inject(method = [/*[@]*/"renderFood"/*[!@]*/], at = [At("RETURN")])
    private fun onPostRenderFoodLevel(guiGraphics: GuiGraphics, player: Player, x: Int, y: Int, ci: CallbackInfo) {
        if (!FoodSaturationOverlay.isEnabled()) return

        val foodData = player.foodData
        val currentFood = foodData.foodLevel.toInt()
        val currentSaturation = foodData.saturationLevel.toFloat()

        val screenWidth = mc().window.guiScaledWidth.toInt()
        val screenHeight = mc().window.guiScaledHeight.toInt()

        // Food bar position (vanilla layout — right-to-left)
        val startX = screenWidth / 2 + 91
        val startY = screenHeight - 39
        val iconSize = 9  // each food icon is 9×9 pixels
        val step = 9      // distance between shank centers

        // Check if the world is hardcore — the food icons use a
        // different texture region offset by 36 px in u.
        val isHardcore = mc().level?.levelData?.isHardcore ?: false
        val hardU = if (isHardcore) 36 else 0

        // ================================================================
        //  1. Saturation overlay — gold-tinted food icons
        //     Draw actual food-icon textures (full/half) with a gold alpha
        //     tint ON TOP of the already-rendered vanilla food bar.
        // ================================================================
        if (FoodSaturationOverlay.showSaturationBar.get() && currentSaturation > 0.0f) {
            // Saturation is measured in half-shanks (0…20).
            // Convert to whole shank count (0…10).
            val saturatedShanks = (currentSaturation / 2.0f).coerceAtMost(10f).toInt()
            for (i in 0 until saturatedShanks) {
                // Right-to-left: index 0 = rightmost (fullest)
                val sx = startX - i * step - iconSize
                if (sx < 0) break

                // Does this shank have both halves filled?
                val saturationAtThisShank = currentSaturation - (i * 2)
                val isFullShank = saturationAtThisShank >= 2.0f

                if (isFullShank) {
                    // Full food icon, gold-tinted (ARGB)
                    guiGraphics.drawTexture(
                        GUI_ICONS, sx, startY,
                        61f + hardU, 27f,
                        iconSize, iconSize, iconSize, iconSize, 256, 256,
                        0xBBFFD700.toInt() // bright gold, 73% alpha
                    )
                } else {
                    // Half food icon, darker gold tint (ARGB)
                    guiGraphics.drawTexture(
                        GUI_ICONS, sx, startY,
                        52f + hardU, 27f,
                        iconSize, iconSize, iconSize, iconSize, 256, 256,
                        0x88B8860B.toInt() // dark gold, 53% alpha
                    )
                }
            }
        }

        // ================================================================
        //  2. Food preview — orange-tinted food icons beyond current hunger
        //     When holding a food item, show how many additional shanks
        //     would be restored, using orange-tinted food icons.
        // ================================================================
        if (FoodSaturationOverlay.showFoodPreview.get()) {
            val heldFood = getHeldEdible()
            val foodProps: FoodProperties = heldFood?.get(DataComponents.FOOD) ?: return

            val nutrition = foodProps.nutrition().toInt()
            if (nutrition <= 0) return

            // Current food in full-shank units
            val currentFullShanks = currentFood / 2

            // How many additional half-shanks would be restored
            // (capped at the empty slots on the bar)
            val previewHalfShanks = nutrition.coerceAtMost(20 - currentFood)
            val previewFullShanks = previewHalfShanks / 2
            val showHalfShank = previewHalfShanks % 2 == 1

            // Draw full orange-tinted food icons for each restored shank
            for (i in 0 until previewFullShanks) {
                val shankIdx = currentFullShanks + i
                if (shankIdx >= 10) break

                val sx = startX - shankIdx * step - iconSize
                if (sx < 0) break

                guiGraphics.drawTexture(
                    GUI_ICONS, sx, startY,
                    61f + hardU, 27f,
                    iconSize, iconSize, iconSize, iconSize, 256, 256,
                    0x99FF5500.toInt() // orange-red, 60% alpha
                )
            }

            // Draw half-shank indicator if odd nutrition
            // Use the half-food icon from the texture sheet
            if (showHalfShank && currentFullShanks + previewFullShanks < 10) {
                val shankIdx = currentFullShanks + previewFullShanks
                val sx = startX - shankIdx * step - iconSize
                if (sx >= 0) {
                    guiGraphics.drawTexture(
                        GUI_ICONS, sx, startY,
                        52f + hardU, 27f,
                        iconSize, iconSize, iconSize, iconSize, 256, 256,
                        0x99FF5500.toInt() // orange-red, 60% alpha
                    )
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