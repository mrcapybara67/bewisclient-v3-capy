package net.bewis09.bewisclient.features.screenshot

import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.settings.structure.Feature

object ScreenshotSettings : Feature(createIdentifier("bewisclient", "screenshot")) {
    val redirect = boolean("redirect", true)
}