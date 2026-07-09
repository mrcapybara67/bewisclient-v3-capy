package net.bewis09.capyclient.game.keybinds

import com.mojang.blaze3d.platform.InputConstants
import net.bewis09.capyclient.game.translations.Translation
import net.minecraft.client.KeyMapping

open class Keybind(default: Int, id: String, name: String, val action: (() -> Unit)?, val tick: ((isPressed: Boolean) -> Unit)? = null) {
    constructor(default: Int, id: String, name: String, action: (() -> Unit)) : this(default, id, name, action = action, tick = null)

    val keyBinding = KeyMapping(Translation("key.$id", name).getKey(), InputConstants.Type.KEYSYM, default, KeybindingImplementer.category)

    fun isPressed(): Boolean = keyBinding.isDown
}