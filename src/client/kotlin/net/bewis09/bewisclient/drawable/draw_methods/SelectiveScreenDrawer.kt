package net.bewis09.bewisclient.drawable.draw_methods

import net.bewis09.bewisclient.impl.settings.OptionsMenuSettings.minecraftyOptionsMenu

val SelectiveScreenDrawer: DrawMethods
    get() = if (minecraftyOptionsMenu.get()) minecraftyMethods else flatMethods

val minecraftyMethods = MinecraftyMethods
val flatMethods = FlatMethods