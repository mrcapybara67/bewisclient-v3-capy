package net.bewis09.bewisclient.settings.impl

import net.bewis09.bewisclient.settings.types.ObjectSetting
import net.bewis09.bewisclient.widget.WidgetLoader

object WidgetSettings : ObjectSetting() {
    init {
        create("defaults", DefaultWidgetSettings)
        create("widgets", WidgetLoader)
    }
}
