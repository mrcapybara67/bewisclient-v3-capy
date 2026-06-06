package net.bewis09.bewisclient.settings.impl

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.settings.structure.Feature
import net.bewis09.bewisclient.widget.WidgetLoader

object WidgetSettings : Feature(createIdentifier("bewisclient", "widgets")) {
    init {
        create("defaults", DefaultWidgetSettings)
        create("widgets", WidgetLoader)
    }
}
