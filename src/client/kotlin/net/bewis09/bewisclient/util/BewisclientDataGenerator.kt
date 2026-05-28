package net.bewis09.bewisclient.util

import net.bewis09.bewisclient.common.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

/**
 * The Data Generator entrypoint for Bewisclient.
 * This is mostly used for translations
 */
object BewisclientDataGenerator : DataGeneratorEntrypoint {
    val translations = hashMapOf<String, String>()

    val datagenEnabled = System.getProperty("fabric-api.datagen") != null

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        EventEntrypoint.onAllEventEntrypoints(EventEntrypoint::onDatagen)

        fabricDataGenerator.createPack().apply {
            addProvider(::BewisclientEnglishLangProvider)
        }
    }

    class BewisclientEnglishLangProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : FabricLanguageProvider(dataOutput, "en_us", registryLookup) {
        override fun generateTranslations(wrapperLookup: HolderLookup.Provider, translationBuilder: TranslationBuilder) {
            translations.forEach(translationBuilder::add)
        }
    }
}