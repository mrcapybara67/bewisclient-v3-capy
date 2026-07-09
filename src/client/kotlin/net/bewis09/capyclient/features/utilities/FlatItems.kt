package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

/**
 * Flat (2D) Items module — replaces the 3D spinning dropped-item model
 * with a flat 2D sprite render.  This can significantly improve FPS on
 * servers with hundreds of dropped items because the GPU skips the
 * per-frame model transforms and texture lookups for every item entity
 * and instead renders a single flat sprite per item stack.
 *
 * The actual render override is performed by [FlatItemsMixin], which
 * replaces the ItemEntity render function with one that draws the item's
 * sprite as a billboard-style flat quad.
 */
object FlatItems : ImageFeature(createIdentifier("capyclient", "flat_items"), "2D Items") {
    /**
     * Size of the flat sprite in GUI-pixels.  Vanilla 3D items are roughly
     * 12 × 12 screen pixels at default distance; the flat replacement can
     * be made slightly larger (easier to see) or smaller (less obtrusive).
     */
    val spriteSize = int("sprite_size", 16, 8, 32)

    /**
     * When enabled, the item sprite always faces the player (billboard)
     * rather than having a fixed world-space orientation.  This makes
     * items visible from any angle.
     */
    val billboard = boolean("billboard", true)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, spriteSize, "sprite_size",
            "Sprite Size",
            "The size of the flat item sprite in screen pixels. " +
                "16 ≈ vanilla-gui-size, 8 = tiny, 32 = very large.",
            "sprite_size"
        )
        list.addRenderable(
            this, billboard, "billboard",
            "Always face player (billboard)",
            "When enabled, the dropped item always rotates to face the camera. " +
                "When disabled, items keep a fixed world-space orientation.",
            "billboard"
        )
    }
}
