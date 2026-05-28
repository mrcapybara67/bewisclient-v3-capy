package net.bewis09.bewisclient.widget

import net.bewis09.bewisclient.common.Identifier
import net.bewis09.bewisclient.common.catch
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.renderables.options_structure.addToQuickSettings
import net.bewis09.bewisclient.drawable.renderables.screen.HudEditScreen
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.drawable.screen_drawing.transform
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.logic.RenderableCreator
import net.bewis09.bewisclient.settings.structure.DescriptionFeature
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.settings.types.FloatSetting
import net.bewis09.bewisclient.settings.types.WidgetPositionSetting
import net.bewis09.bewisclient.widget.impl.CustomWidget
import net.bewis09.bewisclient.widget.logic.WidgetPosition

abstract class Widget(val id: Identifier, title: String, description: String) : DescriptionFeature(
    Translation(id.namespace, "widget.${id.path}.name", title), Translation(id.namespace, "widget.${id.path}.description", description)
) {
    var position: WidgetPositionSetting = create("position", WidgetPositionSetting(defaultPosition()))

    override val settingRenderables: Array<Renderable>
        get() = arrayListOf<Renderable>().also(::appendSettingsRenderables).toTypedArray()

    fun createTranslation(key: String, @Suppress("LocalVariableName") en_us: String) = Translation(id.namespace, "widget.${id.path}.$key", en_us)

    open fun isEnabledByDefault(): Boolean = true

    open fun isHidden(): Boolean = false

    fun isShowing(): Boolean {
        return isEnabled() && (!isHidden() || (util.getCurrentRenderableScreen()?.renderable is HudEditScreen))
    }

    abstract fun defaultPosition(): WidgetPosition

    fun renderScaled(screenDrawing: ScreenDrawing) {
        screenDrawing.transform(getX(), getY(), getScale()) {
            catch { render(screenDrawing) } ?: run {
                error("Error rendering widget $id - disabling it to prevent further errors")
                enabled.set(false)
            }
        }
    }

    abstract fun render(screenDrawing: ScreenDrawing)

    fun getScaledWidth(): Float = getWidth() * getScale()
    fun getScaledHeight(): Float = getHeight() * getScale()

    abstract fun getWidth(): Int
    abstract fun getHeight(): Int
    open fun getScale() = 1.0f

    fun getX(): Float {
        val x = catch { position.get().getX(this) } ?: 0f
        return x.coerceAtLeast(0f).coerceAtMost(screenWidth - getScaledWidth())
    }

    fun getY(): Float {
        val y = catch { position.get().getY(this) } ?: 0f
        return y.coerceAtLeast(0f).coerceAtMost(screenHeight - getScaledHeight())
    }

    open fun getCustomWidgetDataPoints(): List<CustomWidget.WidgetStringData> = listOf()

    open fun appendSettingsRenderables(list: ArrayList<Renderable>) {}

    fun isInBox(mouseX: Double, mouseY: Double) = getX() < mouseX && getX() + getScaledWidth() > mouseX && getY() < mouseY && getY() + getScaledHeight() > mouseY

    fun ArrayList<Renderable>.addRenderable(setting: RenderableCreator<*>, id: String, title: String, description: String? = null, quickSettingsId: String? = null) {
        val renderable = setting.createRenderable("widget.$id", title, description)
        if (quickSettingsId != null) renderable.addToQuickSettings("widget.${id}.name", quickSettingsId)
        this.add(renderable)
    }

    fun ArrayList<Renderable>.addColorRenderable(setting: ColorSetting, alpha: FloatSetting, id: String, title: String, description: String? = null, quickSettingsId: String? = null) {
        val renderable = setting.createRenderableWithFader("widget.$id", title, description, alpha)
        if (quickSettingsId != null) renderable.addToQuickSettings("widget.${id}.name", quickSettingsId)
        this.add(renderable)
    }
}
