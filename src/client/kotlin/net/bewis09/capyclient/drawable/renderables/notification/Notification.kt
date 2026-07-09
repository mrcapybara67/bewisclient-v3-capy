package net.bewis09.capyclient.drawable.renderables.notification

import net.bewis09.capyclient.drawable.Renderable

abstract class Notification() : Renderable() {
    abstract val progress: Float
}