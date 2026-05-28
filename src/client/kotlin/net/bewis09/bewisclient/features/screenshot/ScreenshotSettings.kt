package net.bewis09.bewisclient.features.screenshot

import net.bewis09.bewisclient.settings.types.ObjectSetting

object ScreenshotSettings : ObjectSetting() {
    val redirect = boolean("redirect", true)
}