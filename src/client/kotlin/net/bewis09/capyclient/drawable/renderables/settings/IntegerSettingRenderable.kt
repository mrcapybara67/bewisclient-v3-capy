package net.bewis09.capyclient.drawable.renderables.settings

import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.settings.logic.SettingInterfaceWithDefault
import net.bewis09.capyclient.util.number.Precision

class IntegerSettingRenderable(title: Translation, description: Translation?, setting: SettingInterfaceWithDefault<Int>, min: Int, max: Int) : FaderSettingRenderable<Int>(title, description, setting, Precision(min.toFloat(), max.toFloat(), 1f, 0), { original: Float -> original.toInt() })