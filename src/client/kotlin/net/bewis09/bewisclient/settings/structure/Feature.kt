package net.bewis09.bewisclient.settings.structure

import net.bewis09.bewisclient.common.Identifier
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.features.sidebar.Home.addToQuickSettings
import net.bewis09.bewisclient.settings.types.ObjectSetting

open class Feature(val id: Identifier): ObjectSetting() {
    fun Renderable.addToQuickSettings(feature: Feature, quickSettingsId: String): Renderable {
        return addToQuickSettings("${feature.id.namespace}.menu.category.${feature.id.path}", quickSettingsId)
    }
}