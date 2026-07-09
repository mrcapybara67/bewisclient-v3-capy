package net.bewis09.capyclient.widget.types

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.features.sidebar.Widgets
import net.bewis09.capyclient.settings.structure.Feature
import net.bewis09.capyclient.settings.types.FloatSetting
import net.bewis09.capyclient.widget.Widget

abstract class ScalableWidget(id: Identifier, title: String, description: String) : Widget(id, title, description) {
    var scale = create("scale", FloatSetting({ getDefaultScale() }, Widgets.Default.scale.precision))

    companion object {
        val feature = Feature(createIdentifier("capyclient", "widget.scalable_widget"))
    }

    open fun getDefaultScale(): Float = Widgets.Default.scale.get()

    override fun getScale(): Float {
        return scale.get()
    }

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.add(scale.createRenderable(feature, "scale", "Scale", "Set the scale of the widget"))
    }
}
