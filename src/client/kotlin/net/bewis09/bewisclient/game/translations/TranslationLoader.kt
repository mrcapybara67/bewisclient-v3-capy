package net.bewis09.bewisclient.game.translations

import net.bewis09.bewisclient.api.APIEntrypointLoader
import net.bewis09.bewisclient.drawable.SettingStructure
import net.bewis09.bewisclient.drawable.renderables.popup.AddWidgetPopup
import net.bewis09.bewisclient.drawable.renderables.popup.TiwylaLinesSettingsPopup
import net.bewis09.bewisclient.drawable.renderables.screen.HudEditScreen
import net.bewis09.bewisclient.drawable.renderables.screen.OptionScreen
import net.bewis09.bewisclient.drawable.renderables.screen.PackListScreen
import net.bewis09.bewisclient.features.cosmetics.AcceptPrivacyPage
import net.bewis09.bewisclient.features.cosmetics.EnableOnlineModeSettingsRenderable
import net.bewis09.bewisclient.features.screenshot.BigScreenshotViewElement
import net.bewis09.bewisclient.game.BewisclientResourcePack
import net.bewis09.bewisclient.server.Modrinth
import net.bewis09.bewisclient.settings.structure.CategorizedFeature
import net.bewis09.bewisclient.util.EventEntrypoint
import net.bewis09.bewisclient.util.color.colors
import net.bewis09.bewisclient.widget.WidgetLoader

@Suppress("unusedExpression")
object TranslationLoader : EventEntrypoint {
    override fun onDatagen() {
        OptionScreen()
        SettingStructure
        colors
        AddWidgetPopup
        HudEditScreen
        TiwylaLinesSettingsPopup
        Modrinth
        WidgetLoader.widgets.map(CategorizedFeature::getSettingRenderables)
        APIEntrypointLoader.mapEntrypoint { it.getUtilities().map(CategorizedFeature::getSettingRenderables) }
        BewisclientResourcePack
        BigScreenshotViewElement.Companion
        PackListScreen.Companion
        AcceptPrivacyPage
        EnableOnlineModeSettingsRenderable.Companion
    }
}