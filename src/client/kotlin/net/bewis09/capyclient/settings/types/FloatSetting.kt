package net.bewis09.capyclient.settings.types

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import net.bewis09.capyclient.drawable.renderables.settings.FloatSettingRenderable
import net.bewis09.capyclient.drawable.renderables.settings.IntegerSettingRenderable
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.settings.logic.RenderableCreator
import net.bewis09.capyclient.settings.logic.SettingInterfaceWithDefault
import net.bewis09.capyclient.settings.structure.Feature
import net.bewis09.capyclient.util.float
import net.bewis09.capyclient.util.number.Precision

class FloatSetting(default: () -> Float, val precision: Precision) : Setting<Float>(default), RenderableCreator<FloatSettingRenderable> {
    override fun convertToElement(): JsonElement? {
        return getWithoutDefault()?.let { JsonPrimitive(it) }
    }

    override fun convertFromElement(data: JsonElement?): Float? = processChange(data?.float())

    override fun createRenderable(
        feature: Feature, id: String, title: String, description: String?
    ) = FloatSettingRenderable(
        feature.createTranslation(id, title), description?.let { Translation("$id.description", it) }, this, precision
    )

    fun createIntRenderable(
        feature: Feature, id: String, title: String, description: String? = null
    ): IntegerSettingRenderable = IntegerSettingRenderable(
        feature.createTranslation(id, title), description?.let { feature.createTranslation("$id.description", it) }, object : SettingInterfaceWithDefault<Int> {
            override fun set(value: Int?) {
                this@FloatSetting.set(value?.toFloat())
            }

            override fun get(): Int {
                return this@FloatSetting.get().toInt()
            }

            override fun getDefault(): Int {
                return this@FloatSetting.getDefault().toInt()
            }
        }, precision.min.toInt(), precision.max.toInt()
    )

    override fun processChange(value: Float?): Float? = value?.let {
        precision.parse(it)
    }

    fun cloneWithDefault(): FloatSetting = FloatSetting(::get, precision)
}