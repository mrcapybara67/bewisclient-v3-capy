package net.bewis09.capyclient.game.translations

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.drawable.renderables.popup.AddWidgetPopup
import net.bewis09.capyclient.drawable.renderables.popup.TiwylaLinesSettingsPopup
import net.bewis09.capyclient.drawable.renderables.screen.HudEditScreen
import net.bewis09.capyclient.drawable.renderables.screen.OptionScreen
import net.bewis09.capyclient.drawable.renderables.screen.PackListScreen
import net.bewis09.capyclient.features.cosmetics.AcceptPrivacyPage
import net.bewis09.capyclient.features.cosmetics.EnableOnlineModeSettingsRenderable
import net.bewis09.capyclient.game.BewisclientResourcePack
import net.bewis09.capyclient.server.Modrinth
import net.bewis09.capyclient.settings.structure.CategorizedFeature
import net.bewis09.capyclient.settings.structure.SidebarFeature
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.util.color.colors

@Suppress("unusedExpression")
object TranslationLoader : EventEntrypoint {
    override fun onDatagen() {
        OptionScreen()
        colors
        AddWidgetPopup
        HudEditScreen
        TiwylaLinesSettingsPopup
        Modrinth
        APIEntrypointLoader.mapEntrypointForList { it.getWidgets() }.map(CategorizedFeature::getSettingRenderables)
        APIEntrypointLoader.mapEntrypointForList { it.getUtilities() }.map(CategorizedFeature::getSettingRenderables)
        APIEntrypointLoader.mapEntrypointForList { it.getSidebarCategories() }.map(SidebarFeature::getRenderable)
        BewisclientResourcePack
        PackListScreen.Companion
        AcceptPrivacyPage
        EnableOnlineModeSettingsRenderable.Companion
    }

    override fun onMinecraftClientInitFinished() {
        onDatagen()
    }
}