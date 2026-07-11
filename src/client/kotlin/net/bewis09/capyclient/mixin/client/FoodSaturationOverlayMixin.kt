// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.features.utilities.FoodSaturationOverlay
import net.bewis09.capyclient.version.GuiGraphics
import net.bewis09.capyclient.version.Hud
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
 * 1. **Saturation overlay** — gold-tinted empty food icons drawn ON TOP
 *    of the existing hunger bar to show the player's current saturation.
 * 2. **Food preview** — when holding a food item, orange-tinted food icons
 *    are drawn beyond the current hunger level showing how much hunger
 *    would be restored by eating the held food.
 *
 * Vanilla food bar layout (right-to-left):
 *   startX = screenWidth / 2 + 91   (rightmost shank)
 *   y = screenHeight - 39           (bottom, above hotbar)
 *   Each icon is 9×9 px with a 9 px step (right-to-left).
 */
@Mixin(Hud::class)
abstract class FoodSaturationOverlayMixin {

    @Unique
    private fun mc(): Minecraft = Minecraft.getInstance()

    /**
     * Inject AFTER the food level is rendered to draw the saturation
     * overlay and food preview using modern HUD sprites.
     */
    // @[1.21.8] "renderFoodLevel" @[] "renderFood"
    @Inject(method = [/*[@]*/"renderFood"/*[!@]*/], at = [At("RETURN")])
    private fun onPostRenderFoodLevel(guiGraphics: GuiGraphics, player: Player, x: Int, y: Int, ci: CallbackInfo) {
        if (!FoodSaturationOverlay.isEnabled()) return

        val foodData = player.foodData
        val currentFood = foodData.foodLevel
        val currentSaturation = foodData.saturationLevel

        val screenWidth = mc().window.guiScaledWidth
        val screenHeight = mc().window.guiScaledHeight

        // Food bar position (vanilla layout — right-to-left)
        val startX = screenWidth / 2 + 91
        val startY = screenHeight - 39
        val iconSize = 9  // each food icon is 9×9 pixels
        val step = 9      // distance between shank centers

        // Modern HUD sprites (vanilla does not have hardcore variants for food).
        val emptySprite = Identifier.withDefaultNamespace("hud/food_empty")
        val fullSprite = Identifier.withDefaultNamespace("hud/food_full")
        val halfSprite = Identifier.withDefaultNamespace("hud/food_half")

        // ================================================================
        //  1. Saturation overlay — gold outline on the vanilla drumsticks
        //     AppleSkin-style: draw the EMPTY food sprite (drumstick outline)
        //     in gold on TOP of the already-rendered vanilla food bar.
        //     The outline shrinks right-to-left as saturation depletes.
        // ================================================================
        if (FoodSaturationOverlay.showSaturationBar.get() && currentSaturation > 0.0f) {
            // Saturation is in half-shank units (0…20). Use ceiling so a
            // partial shank still gets drawn.
            val saturatedShanks = kotlin.math.ceil(currentSaturation / 2.0f).coerceAtMost(10f).toInt()
            for (i in 0 until saturatedShanks) {
                val sx = startX - i * step - iconSize
                if (sx < 0) break

                val saturationAtThisShank = currentSaturation - (i * 2)
                // The last (leftmost) shank may be partial.
                val isFullShank = saturationAtThisShank >= 2.0f

                // AppleSkin-style: tint the empty drumstick outline gold.
                // Full shanks are bright; the trailing partial shank is faded.
                val color = if (isFullShank) 0xCCFFC000.toInt()  // bright gold, 80% alpha
                            else 0x66FFD700.toInt()              // faded gold, 40% alpha

                guiGraphics.blitSprite(
                    net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                    emptySprite,
                    sx, startY,
                    iconSize, iconSize,
                    color
                )
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

            // Total hunger after eating, in half-shank units (0…20).
            val previewTotalHalfShanks = (currentFood + nutrition).coerceAtMost(20)
            // Number of half-shanks that are currently empty and will be filled.
            val previewHalfShanks = previewTotalHalfShanks - currentFood
            if (previewHalfShanks <= 0) return

            // Iterate over the preview range in half-shank steps so odd
            // current hunger values are handled correctly.
            var remaining = previewHalfShanks
            var shankIdx = currentFood / 2
            var drawHalf = currentFood % 2 != 0

            while (remaining > 0 && shankIdx < 10) {
                val sx = startX - shankIdx * step - iconSize
                if (sx < 0) break

                if (drawHalf) {
                    // First preview step completes the current half-shank.
                    guiGraphics.blitSprite(
                        net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                        halfSprite,
                        sx, startY,
                        iconSize, iconSize,
                        0x99FF5500.toInt() // orange-red, 60% alpha
                    )
                    remaining -= 1
                    drawHalf = false
                } else if (remaining >= 2) {
                    guiGraphics.blitSprite(
                        net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                        fullSprite,
                        sx, startY,
                        iconSize, iconSize,
                        0x99FF5500.toInt() // orange-red, 60% alpha
                    )
                    remaining -= 2
                } else {
                    guiGraphics.blitSprite(
                        net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                        halfSprite,
                        sx, startY,
                        iconSize, iconSize,
                        0x99FF5500.toInt() // orange-red, 60% alpha
                    )
                    remaining -= 1
                }
                shankIdx++
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