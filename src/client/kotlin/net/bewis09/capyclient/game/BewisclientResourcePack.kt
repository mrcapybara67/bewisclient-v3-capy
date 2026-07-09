package net.bewis09.capyclient.game

import net.bewis09.capyclient.api.APIEntrypointLoader
import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.common.IndependentResourceMetadataSerializer
import net.bewis09.capyclient.common.createIdentifier
import net.bewis09.capyclient.game.translations.Translation
import net.bewis09.capyclient.util.logic.ClientInterface
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
    val capyclientResourcesTitle = Translation("menu.resource_pack.capyclient_resource", "Capy Client Custom Resources")
    val capyclientResourcesDescription = Translation("menu.resource_pack.capyclient_resource_description", "Features can be enabled in the Capy Client settings")

    val packInfo = PackLocationInfo(
        "capyclient_resources",
        capyclientResourcesTitle(),
        PackSource.DEFAULT,
        Optional.empty()
    )

    val metadata = Pack.Metadata(
        capyclientResourcesDescription(),
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
            return IoSupplier { client.resourceManager.getResource(createIdentifier("capyclient", "icon.png")).getOrNull()?.open()!! }
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

    override fun getNamespaces(type: PackType): Set<String> = setOf("capyclient", "minecraft")

    override fun close() {}

    interface CustomResourceProvider {
        fun provideResources(id: Identifier): IoSupplier<InputStream>?
    }
}