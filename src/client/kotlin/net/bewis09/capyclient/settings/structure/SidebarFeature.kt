package net.bewis09.capyclient.settings.structure

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.drawable.Renderable
import net.bewis09.capyclient.drawable.draw_methods.SelectiveScreenDrawer
import net.bewis09.capyclient.drawable.renderables.components.button.ThemeButton
import net.bewis09.capyclient.drawable.renderables.components.element.TextElement
import net.bewis09.capyclient.drawable.renderables.components.structure.Plane
import net.bewis09.capyclient.drawable.renderables.components.structure.VerticalScrollGrid
import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.features.sidebar.General

abstract class SidebarFeature(id: Identifier, titleText: String): Feature(id) {
    val title = Translation(id.namespace, "category.${id.path}", titleText)

    fun createButton(): ThemeButton {
        return ThemeButton(title(), { OptionScreen.currentInstance?.category == id.toString() }, {
            OptionScreen.currentInstance?.changeCategory(this)
        }).setHeight(SelectiveScreenDrawer.getSideButtonHeight()) as ThemeButton
    }

    fun getHeader(): Renderable {
        return Plane { x, y, width, _ -> listOf(TextElement(title(), { General.getTextThemeColor() }, centered = true)(x, y, width, 13)) }.setHeight(14)
    }

    abstract fun getRenderable(): Renderable

    fun createGrid(renderables: List<Renderable>): Renderable {
        return VerticalScrollGrid({ renderables.map { it.setHeight(90) } }, 5, 80)
    }
}