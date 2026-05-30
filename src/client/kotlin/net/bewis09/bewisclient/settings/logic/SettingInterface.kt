package net.bewis09.bewisclient.settings.logic

import net.bewis09.bewisclient.util.interfaces.Gettable
import net.bewis09.bewisclient.util.interfaces.Settable

interface SettingInterface<K> : Settable<K?>, Gettable<K>
interface SettingInterfaceWithDefault<K> : SettingInterface<K> {
    fun getDefault(): K
}