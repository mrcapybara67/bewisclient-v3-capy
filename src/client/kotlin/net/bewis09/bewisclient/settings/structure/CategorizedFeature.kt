package net.bewis09.bewisclient.settings.structure

import net.bewis09.bewisclient.common.Color
import net.bewis09.bewisclient.common.Identifier
import net.bewis09.bewisclient.common.color
import net.bewis09.bewisclient.drawable.Animator
import net.bewis09.bewisclient.drawable.Renderable
import net.bewis09.bewisclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.bewisclient.drawable.renderables.components.element.TextElement
import net.bewis09.bewisclient.drawable.renderables.components.element.TooltipHoverableText
import net.bewis09.bewisclient.drawable.renderables.components.logic.Hoverable
import net.bewis09.bewisclient.drawable.renderables.components.structure.Plane
import net.bewis09.bewisclient.drawable.renderables.components.structure.VerticalAlignScrollPlane
import net.bewis09.bewisclient.drawable.renderables.screen.OptionScreen
import net.bewis09.bewisclient.drawable.renderables.settings.BooleanSettingRenderable
import net.bewis09.bewisclient.drawable.screen_drawing.ScreenDrawing
import net.bewis09.bewisclient.drawable.screen_drawing.pushColor
import net.bewis09.bewisclient.drawable.screen_drawing.translate
import net.bewis09.bewisclient.features.sidebar.General
import net.bewis09.bewisclient.game.translations.Translation
import net.bewis09.bewisclient.settings.logic.RenderableCreator
import net.bewis09.bewisclient.settings.types.ColorSetting
import net.bewis09.bewisclient.settings.types.FloatSetting

abstract class CategorizedFeature(id: Identifier, titleText: String) : Feature(id) {
    companion object {
        val clickToEnableText = Translation("menu.general.enable", "Click to Enable")
        val clickToDisableText = Translation("menu.general.disable", "Click to Disable")
        val enabledText = Translation("menu.general.enabled", "Enabled")
        val disabledText = Translation("menu.general.disabled", "Disabled")
    }

    val title = Translation(id.namespace, "menu.category.${id.path}", titleText)

    open val enabledByDefault = false
    val enabled = boolean("enabled", enabledByDefault) { oldValue, newValue -> enabledListener(oldValue, newValue) }

    fun getSettingRenderables(): Array<Renderable> = arrayListOf<Renderable>().also(::appendSettingsRenderables).toTypedArray()

    open fun appendSettingsRenderables(list: ArrayList<Renderable>) {}

    open fun enabledListener(oldValue: Boolean?, newValue: Boolean?) {}

    fun isEnabled(): Boolean = enabled.get()

    abstract fun createRenderable(): SettingCategory

    fun getHeader(): Renderable {
        return Plane { x, y, width, _ -> listOf(TextElement(title(), General.getTextThemeColor(), centered = true)(x, y, width, 13)) }.setHeight(14)
    }

    open fun getPane(): Renderable {
        return VerticalAlignScrollPlane(getSettingRenderables().toList(), 1)
    }

    abstract inner class SettingCategory() : Hoverable() {
        val state = Animator({ animationDuration }, Animator.EASE_IN_OUT, if (enabled.get()) 1f else 0f)

        init {
            BooleanSettingRenderable(enabledText, null, enabled).addToQuickSettings(this@CategorizedFeature, "enabled")
        }

        override fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean {
            if (getSettingRenderables().isEmpty()) {
                this@CategorizedFeature.enabled.toggle()
                resize()
                return true
            }

            OptionScreen.currentInstance?.openPage(getHeader(), getPane(), enabled)

            return true
        }

        abstract fun renderContent(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int)

        override fun render(screenDrawing: ScreenDrawing, mouseX: Int, mouseY: Int) {
            state.set(if (enabled.get()) 1f else 0f)
            super.render(screenDrawing, mouseX, mouseY)

            val s = (state.get().coerceAtLeast(hoverFactor / 3)) * 1f

            SelectiveScreenDrawer.renderSettingsCategoryBackground(screenDrawing, x, y, width, height, state.get(), hoverFactor, mouseX, mouseY)

            val t = 1 - (1 - s) / 2.5f

            screenDrawing.translate(0f, if (isMinecrafty) -3f else 0f) {
                screenDrawing.pushColor(t, t, t, 1f) {
                    renderContent(screenDrawing, mouseX, mouseY)
                    renderRenderables(screenDrawing, mouseX, mouseY)
                }
            }
        }

        override fun init() {
            super.init()
            addRenderable(
                TooltipHoverableText(
                    if (enabled.get()) enabledText() else disabledText(), 0xAAAAAA.color, Color.WHITE, if (enabled.get()) clickToDisableText() else clickToEnableText(), true
                ) { enabled.toggle(); resize() }(
                    x, y2 - 14, width, 14
                )
            )
        }
    }

    fun ArrayList<Renderable>.addRenderable(feature: CategorizedFeature, setting: RenderableCreator<*>, id: String, title: String, description: String? = null, quickSettingsId: String? = null) {
        val renderable = setting.createRenderable(id, title, description)
        if (quickSettingsId != null) renderable.addToQuickSettings(feature, quickSettingsId)
        this.add(renderable)
    }

    fun ArrayList<Renderable>.addColorRenderable(feature: CategorizedFeature, setting: ColorSetting, alpha: FloatSetting, id: String, title: String, description: String? = null, quickSettingsId: String? = null) {
        val renderable = setting.createRenderableWithFader(id, title, description, alpha)
        if (quickSettingsId != null) renderable.addToQuickSettings(feature, quickSettingsId)
        this.add(renderable)
    }
}