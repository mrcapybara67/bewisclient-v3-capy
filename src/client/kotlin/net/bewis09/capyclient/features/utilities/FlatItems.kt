package net.bewis09.capyclient.features.utilities

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.settings.structure.ImageFeature

/**
 * Flat (2D) Items module — makes dropped items render as flat,
 * non-spinning sprites instead of the vanilla 3D spinning model.
 *
 * How it works:
 * - **Billboard mode** (default ON): items always face the nearest
 *   player, like a real-world billboard.  This makes them visible
 *   from any angle.
 * - **Static mode** (billboard OFF): items freeze in place with no
 *   rotation — no spinning, no bobbing, just a static flat sprite.
 *
 * The actual effect is implemented by [FlatItemsMixin], which
 * intercepts [net.minecraft.world.entity.item.ItemEntity.tick]
 * and overrides the rotation every frame.
 *
 * Performance benefit: on servers with hundreds of dropped items
 * the GPU skips the per-frame model transforms for every item
 * entity because the rotation is frozen to a constant value
 * (either facing the player or 0).
 */
object FlatItems : ImageFeature(createIdentifier("capyclient", "flat_items"), "2D Items") {
    /**
     * When enabled (default), items continuously rotate to face the
     * nearest player (billboard).  When disabled, items are frozen
     * in place with zero rotation.
     */
    val billboard = boolean("billboard", true)

    /**
     * Placeholder for a future flat-sprite size setting.
     * Currently the item still renders the vanilla 3D model
     * (with frozen rotation).  This setting will control the
     * pixel size of the 2D sprite replacement once the
     * billboard-quad renderer is implemented.
     */
    val spriteSize = int("sprite_size", 16, 8, 32)

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.addRenderable(
            this, billboard, "billboard",
            "Always face player (billboard)",
            "When enabled, the dropped item always rotates to face the camera. " +
                "When disabled, items keep a fixed zero-rotation orientation.",
            "billboard"
        )
        list.addRenderable(
            this, spriteSize, "sprite_size",
            "Sprite Size",
            "The size of the flat item sprite in screen pixels. " +
                "16 ≈ vanilla-gui-size, 8 = tiny, 32 = very large. " +
                "Note: 3D→2D sprite rendering coming in a future update.",
            "sprite_size"
        )
    }
}
