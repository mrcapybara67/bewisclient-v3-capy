package net.bewis09.capyclient.features.sidebar

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.renderables.components.button.Button
import net.bewis09.capyclient.drawable.renderables.components.element.TextElement
import net.bewis09.capyclient.drawable.renderables.components.structure.Plane
import net.bewis09.capyclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.capyclient.drawable.renderables.components.structure.VerticalScrollGrid
import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.settings.structure.CategorizedFeature
import net.bewis09.capyclient.settings.structure.Feature
import net.bewis09.capyclient.settings.structure.SidebarFeature
import net.bewis09.capyclient.settings.types.ColorSetting
import net.bewis09.capyclient.util.color.StaticColorSaver
import net.bewis09.capyclient.widget.WidgetLoader
import net.bewis09.capyclient.widget.WidgetLoader.widgets

object Widgets : SidebarFeature(createIdentifier("capyclient", "widgets"), "Widgets") {
    init {
        create("defaults", Default)
        create("widgets", WidgetLoader)
    }

    val widgetRenderables = widgets.map(CategorizedFeature::createRenderable)

    val generalWidgetSettings = APIEntrypointLoader.mapEntrypoint { it.getGeneralWidgetSettings() }.flatten()

    val widgetsPlane = Plane { x, y, width, height ->
        listOf(
            Button(createTranslation("general_setting", "General Widget Settings")()) {
                OptionScreen.currentInstance?.openPage(
                    TextElement(createTranslation("general_setting", "General Widget Settings")(), centered = true).setHeight(12), VerticalAlignScrollPlane({ generalWidgetSettings }, 1)
                )
            }(x, y, width, SelectiveScreenDrawer.getSideButtonHeight()),
//            Button(Translation("menu.widgets.presets", "Presets")()) {
//
//            }(x + width - 55, 37, 55, 14),
            VerticalScrollGrid({ widgetRenderables.map { a -> a.setHeight(90) } }, 5, 80).invoke(x, y + SelectiveScreenDrawer.getSideButtonHeight() + 5, width, height - SelectiveScreenDrawer.getSideButtonHeight() - 5)
        )
    }

    override fun getRenderable(): Renderable = widgetsPlane

    object Default : Feature(createIdentifier("capyclient", "widgets_defaults")) {
        val backgroundColor = color("background_color", StaticColorSaver(0f, 0f, 0f), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
        val backgroundOpacity = float("background_opacity", 0.5f, 0f, 1f, 0.01f, 2)
        val borderColor = color("border_color", StaticColorSaver(0f, 0f, 0f), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
        val borderOpacity = float("border_opacity", 0f, 0f, 1f, 0.01f, 2)
        val paddingSize = int("padding_size", 5, 0, 10)
        val lineSpacing = int("line_spacing", 2, 0, 20)
        val textColor = color("text_color", StaticColorSaver(1f, 1f, 1f), ColorSetting.STATIC, ColorSetting.CHANGING, ColorSetting.THEME)
        val borderRadius = int("border_radius", 0, 0, 20)
        val shadow = boolean("shadow", true)
        val scale = float("scale", .8f, 0.5f, 2f, 0.01f, 2)
        val gap = int("gap", 1, 0, 20)
        val screenEdgeDistance = int("screen_edge_distance", 5, 0, 10)
    }
}