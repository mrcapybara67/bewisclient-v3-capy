package net.bewis09.capyclient.settings.structure

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.features.sidebar.Home.addToQuickSettings
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.settings.types.ObjectSetting

open class Feature(val id: Identifier): ObjectSetting() {
    fun Renderable.addToQuickSettings(feature: Feature, quickSettingsId: String): Renderable {
        return addToQuickSettings("${feature.id.namespace}.menu.category.${feature.id.path}", quickSettingsId)
    }

    open fun createTranslation(key: String, translation: String): Translation {
        return Translation(id.namespace, "${id.path}.${key}", translation)
    }
}