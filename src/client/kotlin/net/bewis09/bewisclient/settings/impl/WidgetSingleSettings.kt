package net.bewis09.bewisclient.settings.impl

import net.bewis09.bewisclient.settings.types.ObjectSetting
import net.bewis09.bewisclient.widget.WidgetLoader

object WidgetSingleSettings : ObjectSetting() {
    init {
        WidgetLoader.widgets.forEach { create(it.id.toString(), it) }
    }
}