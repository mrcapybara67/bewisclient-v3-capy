package net.bewis09.capyclient.widget.impl

import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.common.toText
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.widget.logic.BOTTOM
import net.bewis09.capyclient.widget.logic.RelativePosition
import net.bewis09.capyclient.widget.logic.WidgetPosition
import net.bewis09.capyclient.widget.types.LineWidget
import net.minecraft.world.phys.Vec3


object SpeedWidget : LineWidget(
    createIdentifier("capyclient", "speed_widget"),
    "Speed Widget",
    "Displays your current speed in blocks per second."
), EventEntrypoint {
    val verticalSpeed = boolean("vertical_speed", false)

    var oldPos: Vec3 = Vec3.ZERO
    var horizontalSpeed = 0f
    var totalSpeed = 0f

    override fun getLine() = String.format("%.2f m/s", if (!isInWorld()) if (verticalSpeed.get()) 6.9f else 4.2f else if (verticalSpeed.get()) totalSpeed else horizontalSpeed).toText()

    override fun defaultPosition(): WidgetPosition = RelativePosition(PingWidget, BOTTOM)

    override fun getMinimumWidth(): Int = 80

    override fun isEnabledByDefault(): Boolean = false

    override fun onClientTickStart() {
        client.player?.position()?.let {
            // Compute speed using raw delta components to avoid two Vec3 allocations:
            //   - it.distanceTo(oldPos) creates no Vec3 but uses sqrt internally (unavoidable for actual speed)
            //   - it.subtract(oldPos).horizontalDistance() creates a NEW Vec3 every tick — avoid that
            val dx = it.x - oldPos.x
            val dy = it.y - oldPos.y
            val dz = it.z - oldPos.z
            totalSpeed = kotlin.math.sqrt(dx * dx + dy * dy + dz * dz).toFloat() * 20f
            horizontalSpeed = kotlin.math.sqrt(dx * dx + dz * dz).toFloat() * 20f
            oldPos = it
        }
    }

    override fun appendSettingsRenderables(
        list: ArrayList<Renderable>
    ) {
        list.addRenderable(this, verticalSpeed, "vertical_speed", "Include Vertical Speed", null, "vertical_speed")
        super.appendSettingsRenderables(list)
    }

    override fun getCustomWidgetDataPoints(): List<CustomWidget.WidgetStringData> = listOf(
        CustomWidget.WidgetStringData("horizontal_speed", "Horizontal Speed", "Your current horizontal speed in blocks per second", { String.format("%.2f", if (!isInWorld()) 4.2f else horizontalSpeed).toText() }),
        CustomWidget.WidgetStringData("total_speed", "Total Speed", "Your current total speed in blocks per second", { String.format("%.2f", if (!isInWorld()) 6.9f else totalSpeed).toText() }),
    )
}
