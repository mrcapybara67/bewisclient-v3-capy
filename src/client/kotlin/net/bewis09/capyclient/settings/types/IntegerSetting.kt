package net.bewis09.capyclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.bewis09.capyclient.drawable.renderables.settings.IntegerSettingRenderable
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.settings.logic.RenderableCreator
import net.bewis09.capyclient.settings.structure.Feature
import net.bewis09.capyclient.util.int

class IntegerSetting(default: () -> Int, val min: Int, val max: Int) : Setting<Int>(default), RenderableCreator<IntegerSettingRenderable> {
    override fun convertToElement(): JsonElement? {
        return getWithoutDefault()?.let { JsonPrimitive(it) }
    }

    override fun convertFromElement(data: JsonElement?): Int? = data?.int()

    override fun createRenderable(feature: Feature, id: String, title: String, description: String?) = IntegerSettingRenderable(
        feature.createTranslation(id, title), description?.let { Translation("$id.description", it) }, this, min, max
    )

    fun cloneWithDefault(): IntegerSetting = IntegerSetting(::get, min, max)
}