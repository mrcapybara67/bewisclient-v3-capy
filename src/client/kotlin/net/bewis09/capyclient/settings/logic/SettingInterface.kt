package net.bewis09.capyclient.settings.logic

import net.bewis09.capyclient.util.interfaces.Gettable
import net.bewis09.capyclient.util.interfaces.Settable

interface SettingInterface<K> : Settable<K?>, Gettable<K>
interface SettingInterfaceWithDefault<K> : SettingInterface<K> {
    fun getDefault(): K
}