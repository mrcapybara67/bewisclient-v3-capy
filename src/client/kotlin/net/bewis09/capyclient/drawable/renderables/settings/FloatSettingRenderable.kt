package net.bewis09.capyclient.drawable.renderables.settings

import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.settings.types.Setting
import net.bewis09.capyclient.util.number.Precision

class FloatSettingRenderable(
    title: Translation, description: Translation? = null, setting: Setting<Float>, precision: Precision
) : FaderSettingRenderable<Float>(title, description, setting, precision, { it })