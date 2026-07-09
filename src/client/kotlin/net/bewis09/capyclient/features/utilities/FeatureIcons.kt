package net.bewis09.capyclient.features.utilities

import com.mojang.blaze3d.platform.NativeImage
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.util.EventEntrypoint
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture

/**
 * Registers 40×40 coloured square icons for the feature modules.
 *
 * Registration is deferred to the first client tick so that the
 * render context (OpenGL / Vulkan) is guaranteed to be ready.
 * On Android with VulkanMod, calling DynamicTexture() during
 * Fabric init would crash because the GPU context doesn't exist yet.
 */
object FeatureIcons : EventEntrypoint {

    private var registered = false

    private fun createIcon(name: String, r: Int, g: Int, b: Int) {
        val size = 40
        val img = NativeImage(size, size, false)

        for (y in 0 until size) {
            for (x in 0 until size) {
                val rr = (r * (size - y) + 255 * y) / size
                val gg = (g * (size - x) + 200 * x) / size
                val bb = (b * (x + y) + 100 * (size * size - x - y)) / (size * size)
                val argb = (255 shl 24) or ((rr.coerceIn(0, 255)) shl 16) or ((gg.coerceIn(0, 255)) shl 8) or (bb.coerceIn(0, 255))
                img.setPixel(x, y, argb)
            }
        }

        val id = createIdentifier("capyclient", "textures/gui/features/$name")
        Minecraft.getInstance().textureManager.register(id,
            DynamicTexture({ id.toString() }, img))
    }

    /**
     * Called from the first client tick to ensure the render context is ready.
     */
    fun ensureRegistered() {
        if (registered) return
        registered = true
        createIcon("color_saturation", 255, 102, 0)   // Orange
        createIcon("flat_items", 0, 153, 204)          // Teal
        createIcon("item_physics", 255, 170, 0)        // Gold
        createIcon("no_chat_lag", 221, 51, 51)         // Red
    }

    override fun onInitializeClient() {
        // Don't register here — render context may not be ready yet on Android.
        // Registration happens in onClientTickStart() via ensureRegistered().
    }

    override fun onClientTickStart() {
        ensureRegistered()
    }
}
