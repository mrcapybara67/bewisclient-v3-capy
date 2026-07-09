// @VersionReplacement

package net.bewis09.capyclient.mixin.client

import net.bewis09.capyclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.capyclient.features.utilities.HeldItemTooltip
import net.bewis09.capyclient.features.utilities.Scoreboard
import net.bewis09.capyclient.util.logic.ClientInterface
import net.bewis09.capyclient.version.GuiGraphics
import net.bewis09.capyclient.version.Hud
import net.bewis09.capyclient.version.pop
import net.bewis09.capyclient.version.push
import net.bewis09.capyclient.version.scale
import net.bewis09.capyclient.version.translate
import net.minecraft.client.gui.Font
import net.minecraft.world.item.ItemStack
import net.minecraft.world.scores.Objective
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Hud::class)
abstract class HudMixin : ClientInterface {
    @Shadow
    abstract fun getFont(): Font?

    @Shadow
    private val toolHighlightTimer = 0

    @Shadow
    private val lastToolHighlight: ItemStack? = null

    // @[1.21.11] "renderSelectedItemName" @[] "extractSelectedItemName"
    @Inject(method = [/*[@]*/"renderSelectedItemName"/*[!@]*/], at = [At("HEAD")], cancellable = true)
    private fun capyclientRenderHeldItemTooltip(drawContext: GuiGraphics, ci: CallbackInfo) {
        if (!HeldItemTooltip.isEnabled()) return

        HeldItemTooltip.render(ScreenDrawing(drawContext, getFont()!!), toolHighlightTimer, lastToolHighlight!!)
        ci.cancel()
    }

    // In 1.21.11 with Mojang mappings, the scoreboard has two methods:
    //   - renderScoreboardSidebar(GuiGraphics, DeltaTracker) — the new render
    //     wrapper that takes a DeltaTracker (called from the main render loop)
    //   - displayScoreboardSidebar(GuiGraphics, Objective) — the helper that
    //     draws the actual sidebar content for a specific objective
    // The previous build used `renderScoreboardSidebar` for 1.21.11, but that
    // method takes DeltaTracker, not Objective. The mixin signature has
    // `objective: Objective?` which only matches `displayScoreboardSidebar`.
    // Injection into `renderScoreboardSidebar` would silently fail at runtime
    // (no crash, but the scoreboard push/pop never fires). Use the old method
    // name for all versions since it has the correct parameter types.
    // @[] "displayScoreboardSidebar"
    @Inject(method = [/*[@]*/"displayScoreboardSidebar"/*[!@]*/], at = [At("HEAD")])
    private fun capyclientRenderScoreboardSidebar(guiGraphics: GuiGraphics, objective: Objective?, ci: CallbackInfo?) {
        if (!Scoreboard.isEnabled()) return

        // Both HEAD and RETURN must agree on whether to do a push/pop pair.
        // BUGFIX: the previous build returned early when scale==1.0f, which
        // silently dropped the user-configured offsetX/Y when the user kept
        // the default scale. Now we only skip when nothing would change.
        val scale = Scoreboard.scale.get()
        val offsetX = Scoreboard.offsetX.get()
        val offsetY = Scoreboard.offsetY.get()
        if (scale == 1.0f && offsetX == 0 && offsetY == 0) return

        guiGraphics.push()
        // Re-centre the scaled scoreboard so that the top-right corner stays
        // anchored to the same screen pixel. Without this re-centre, scaling
        // would push the scoreboard toward the origin. The translate uses
        // (1 - 1/scale) so a scale of 1.0 contributes zero (we still skip the
        // push at scale==1.0, but the math stays correct when scale != 1.0).
        if (scale != 1.0f) {
            guiGraphics.scale(scale, scale)
            guiGraphics.translate((-screenWidth).toFloat() * (1.0f - 1 / scale), (-screenHeight).toFloat() * (1.0f - 1 / scale) / 2.0f)
            // Apply the user's pixel offsets in scaled space: 1px = (1/scale) units.
            guiGraphics.translate(offsetX.toFloat() / scale, offsetY.toFloat() / scale)
        } else {
            // Pure translation when no scaling is requested. Push/pop pair is
            // still required so the matrix stack stays balanced.
            guiGraphics.translate(offsetX.toFloat(), offsetY.toFloat())
        }
    }

    // @[] "displayScoreboardSidebar"
    @Inject(method = [/*[@]*/"displayScoreboardSidebar"/*[!@]*/], at = [At("RETURN")])
    private fun capyclientRenderScoreboardSidebarReturn(guiGraphics: GuiGraphics, objective: Objective?, ci: CallbackInfo?) {
        if (!Scoreboard.isEnabled()) return
        // Mirror of capyclientRenderScoreboardSidebar: pop only if we pushed.
        if (Scoreboard.scale.get() == 1.0f && Scoreboard.offsetX.get() == 0 && Scoreboard.offsetY.get() == 0) return
        guiGraphics.pop()
    }
}