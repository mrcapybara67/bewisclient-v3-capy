package net.bewis09.bewisclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.bewis09.bewisclient.drawable.renderables.settings.StringSettingRenderable
import net.bewis09.bewisclient.settings.logic.RenderableCreator
import net.bewis09.bewisclient.settings.structure.Feature
import net.bewis09.bewisclient.util.string

class StringSetting(default: () -> String) : Setting<String>(default), RenderableCreator<StringSettingRenderable> {
    override fun convertToElement(): JsonElement? {
        return getWithoutDefault()?.let { JsonPrimitive(it) }
    }

    override fun convertFromElement(data: JsonElement?): String? = data?.string()

    override fun createRenderable(feature: Feature, id: String, title: String, description: String?): StringSettingRenderable {
        return StringSettingRenderable(
            feature.createTranslation(id, title),
            description?.let { feature.createTranslation("$id.description", it) },
            this,
        )
    }
}
