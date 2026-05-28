package net.bewis09.bewisclient.widget.types

import net.bewis09.bewisclient.common.Identifier
import net.bewis09.bewisclient.common.toText
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.settings.BooleanSettingRenderable
import net.bewis09.bewisclient.drawable.renderables.settings.ColorFaderSettingRenderable
import net.bewis09.bewisclient.drawable.renderables.settings.ColorSettingRenderable
import net.bewis09.bewisclient.drawable.renderables.settings.IntegerSettingRenderable
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.settings.impl.DefaultWidgetSettings
import net.bewis09.bewisclient.settings.types.BooleanSetting
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.settings.types.FloatSetting
import net.bewis09.bewisclient.settings.types.IntegerSetting
import net.minecraft.network.chat.Component

abstract class LineWidget(id: Identifier, title: String, description: String) : ScalableWidget(id, title, description) {
    private var lineWidth = 0

    companion object {
        private val EMPTY = "".toText()

        fun backgroundColorRenderable(backgroundColor: ColorSetting, backgroundOpacity: FloatSetting): ColorFaderSettingRenderable {
            return backgroundColor.createRenderableWithFader("widget.background", "Background", "Set the color and opacity of the widget's background", backgroundOpacity)
        }

        fun borderColorRenderable(borderColor: ColorSetting, borderOpacity: FloatSetting): ColorFaderSettingRenderable {
            return borderColor.createRenderableWithFader("widget.border", "Border", "Set the color and opacity of the widget's border", borderOpacity)
        }

        fun paddingSizeRenderable(paddingSize: IntegerSetting): IntegerSettingRenderable {
            return paddingSize.createRenderable("widget.padding_size", "Padding Size", "Set the padding at the edge of the widget to the text")
        }

        fun lineSpacingRenderable(lineSpacing: IntegerSetting): IntegerSettingRenderable {
            return lineSpacing.createRenderable("widget.line_spacing", "Line Spacing", "Set the spacing between lines of text in the widget")
        }

        fun textColorRenderable(textColor: ColorSetting): ColorSettingRenderable {
            return textColor.createRenderable("widget.text_color", "Text Color", "Set the color of the text in the widget")
        }

        fun borderRadiusRenderable(borderRadius: IntegerSetting): IntegerSettingRenderable {
            return borderRadius.createRenderable("widget.border_radius", "Border Radius", "Set the radius of the widget's border corners")
        }

        fun shadowRenderable(shadow: BooleanSetting): BooleanSettingRenderable {
            return shadow.createRenderable("widget.text_shadow", "Text Shadow", "Set whether text in the widget has a shadow")
        }
    }

    val backgroundColor = create("background_color", DefaultWidgetSettings.backgroundColor.cloneWithDefault())
    val backgroundOpacity = create("background_opacity", DefaultWidgetSettings.backgroundOpacity.cloneWithDefault())
    val borderColor = create("border_color", DefaultWidgetSettings.borderColor.cloneWithDefault())
    val borderOpacity = create("border_opacity", DefaultWidgetSettings.borderOpacity.cloneWithDefault())
    val paddingSize = create("padding_size", DefaultWidgetSettings.paddingSize.cloneWithDefault())
    val shadow = create("shadow", DefaultWidgetSettings.shadow.cloneWithDefault())
    val lineSpacing = create("line_spacing", DefaultWidgetSettings.lineSpacing.cloneWithDefault())
    val textColor = create("text_color", DefaultWidgetSettings.textColor.cloneWithDefault())
    val borderRadius = create("border_radius", DefaultWidgetSettings.borderRadius.cloneWithDefault())

    open fun hasMultipleLines(): Boolean = getLine() === EMPTY

    open fun getLines() = listOf(getLine())
    open fun getLine(): Component = EMPTY

    open fun isCentered(): Boolean = true

    override fun render(screenDrawing: ScreenDrawing) {
        val lines = getLines()
        if (lines.isEmpty()) return

        lineWidth = lines.maxOfOrNull { screenDrawing.getTextWidth(it) }?.plus(2 * paddingSize()) ?: 0

        screenDrawing.fillWithBorderRounded(
            0, 0, getWidth(), getHeight(), borderRadius(), backgroundColor().getColor() alpha backgroundOpacity(), borderColor().getColor() alpha borderOpacity()
        )

        renderAccessories(screenDrawing)

        lines.forEachIndexed { i, line ->
            val y = (i * (9 + lineSpacing())) + paddingSize()
            if (isCentered()) {
                screenDrawing.drawCenteredText(line, getWidth() / 2, y, textColor().getColor(), shadow())
            } else {
                screenDrawing.drawText(line, paddingSize(), y, textColor().getColor(), shadow())
            }
        }
    }

    open fun renderAccessories(screenDrawing: ScreenDrawing) {}

    final override fun getWidth(): Int {
        return lineWidth.coerceIn(getMinimumWidth(), getMaximumWidth())
    }

    abstract fun getMinimumWidth(): Int

    open fun getMaximumWidth(): Int = getMinimumWidth()

    override fun getHeight(): Int {
        val paddingSize = paddingSize.get()
        val lineSpacing = lineSpacing.get()

        val lines = getLines()
        if (lines.isEmpty()) return 0

        return lines.size * (9 + lineSpacing) + 2 * paddingSize - lineSpacing - 2
    }

    override fun appendSettingsRenderables(list: ArrayList<Renderable>) {
        list.add(backgroundColorRenderable(backgroundColor, backgroundOpacity))
        list.add(borderColorRenderable(borderColor, borderOpacity))
        list.add(paddingSizeRenderable(paddingSize))
        if (hasMultipleLines()) list.add(lineSpacingRenderable(lineSpacing))
        list.add(textColorRenderable(textColor))
        list.add(borderRadiusRenderable(borderRadius))
        list.add(shadowRenderable(shadow))
        super.appendSettingsRenderables(list)
    }
}
