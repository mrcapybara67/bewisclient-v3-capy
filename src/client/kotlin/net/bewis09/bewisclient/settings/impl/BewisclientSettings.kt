package net.bewis09.bewisclient.settings.impl

import net.bewis09.bewisclient.settings.logic.Settings
import net.bewis09.bewisclient.settings.logic.Version2Migration

object BewisclientSettings : Settings() {
    override fun getId(): String = "bewisclient"
    override fun getMainSetting() = BewisclientSettingsObject

    override fun load() {
        if (Version2Migration.update()) {
            save()
        }

        super.load()
    }
}