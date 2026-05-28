package net.bewis09.bewisclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.bewis09.bewisclient.drawable.renderables.settings.BooleanSettingRenderable
import net.bewis09.bewisclient.drawable.renderables.settings.MultipleBooleanSettingsRenderable
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.logic.RenderableCreator
import net.bewis09.bewisclient.util.boolean

class BooleanSetting(default: () -> Boolean) : Setting<Boolean>(default), RenderableCreator<BooleanSettingRenderable> {
    override fun convertToElement(): JsonElement? {
        return getWithoutDefault()?.let { JsonPrimitive(it) }
    }

    override fun convertFromElement(data: JsonElement?): Boolean? = data?.boolean()

    fun toggle() = set(not())

    override fun createRenderable(id: String, title: String, description: String?): BooleanSettingRenderable {
        return BooleanSettingRenderable(Translation("menu.$id", title), description?.let { Translation("menu.$id.description", it) }, this)
    }

    fun createRenderablePart(id: String, title: String, description: String? = null): MultipleBooleanSettingsRenderable.Part {
        return MultipleBooleanSettingsRenderable.Part(Translation("menu.$id", title).invoke(), description?.let { Translation("menu.$id.description", it) }(), this)
    }

    fun cloneWithDefault(): BooleanSetting = BooleanSetting(::get)

    operator fun not(): Boolean = !get()
}