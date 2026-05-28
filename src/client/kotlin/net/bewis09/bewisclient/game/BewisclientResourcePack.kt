package net.bewis09.bewisclient.game

import net.bewis09.bewisclient.api.APIEntrypointLoader
import net.bewis09.bewisclient.common.Identifier
import net.bewis09.bewisclient.common.IndependentResourceMetadataSerializer
import net.bewis09.bewisclient.common.createIdentifier
import net.bewis09.bewisclient.drawable.Translations
import net.bewis09.bewisclient.util.logic.ClientInterface
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.Pack.ResourcesSupplier
import net.minecraft.server.packs.repository.PackCompatibility
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.resources.IoSupplier
import net.minecraft.world.flag.FeatureFlagSet
import java.io.InputStream
import java.util.*
import kotlin.jvm.optionals.getOrNull

object BewisclientResourcePack : PackResources, ClientInterface {
    val packInfo = PackLocationInfo(
        "bewisclient_resources",
        Translations.BEWISCLIENT_RESOURCES(),
        PackSource.DEFAULT,
        Optional.empty()
    )

    val metadata = Pack.Metadata(
        Translations.BEWISCLIENT_RESOURCES_DESCRIPTION(),
        PackCompatibility.COMPATIBLE,
        FeatureFlagSet.of(),
        mutableListOf()
    )

    val pack = Pack(
        packInfo,
        object : ResourcesSupplier {
            override fun openPrimary(info: PackLocationInfo): PackResources = BewisclientResourcePack
            override fun openFull(info: PackLocationInfo, metadata: Pack.Metadata): PackResources = openPrimary(info)
        },
        metadata,
        PackSelectionConfig(true, Pack.Position.TOP, true)
    )

    override fun getRootResource(vararg strings: String): IoSupplier<InputStream>? {
        if (strings.contentEquals(arrayOf("pack.png"))) {
            return IoSupplier { client.resourceManager.getResource(createIdentifier("bewisclient", "icon.png")).getOrNull()?.open()!! }
        }
        return null
    }

    override fun listResources(packType: PackType, string: String, string2: String, resourceOutput: PackResources.ResourceOutput) {}

    override fun getResource(packType: PackType, identifier: Identifier): IoSupplier<InputStream>? {
        if (packType != PackType.CLIENT_RESOURCES) return null

        APIEntrypointLoader.mapEntrypoint { it.getCustomResourceProviders() }.forEach { providers ->
            providers.forEach { provider -> provider.provideResources(identifier)?.let { return it } }
        }

        return null
    }

    override fun <T : Any> getMetadataSection(metadataSectionType: IndependentResourceMetadataSerializer<T>): T? = null

    override fun location(): PackLocationInfo = packInfo

    override fun getNamespaces(type: PackType): Set<String> = setOf("bewisclient", "minecraft")

    override fun close() {}

    interface CustomResourceProvider {
        fun provideResources(id: Identifier): IoSupplier<InputStream>?
    }
}