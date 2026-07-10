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
import java.util.Optional
import java.util.Set
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
            val iconId = createIdentifier("capyclient", "icon.png")
            val iconRes = client.resourceManager.getResource(iconId).getOrNull()
            if (iconRes != null) {
                return IoSupplier { iconRes.open() }
            }
            // Icon not found — return null so Minecraft uses the default pack icon.
            return null
        }
        return null
    }

    override fun listResources(packType: PackType, string: String, string2: String, resourceOutput: PackResources.ResourceOutput) {}

    /**
     * Lazily cached list of [CustomResourceProvider]s from API entrypoints.
     * Built once after entrypoints are initialised and reused for the lifetime
     * of the game session so we don't re-allocate+iterate on every vanilla
     * resource lookup — especially important because this pack declares the
     * "minecraft" namespace (needed for Panorama) and therefore gets queried
     * for every single vanilla block/item/GUI texture.
     */
    @Volatile
    private var cachedProviders: List<CustomResourceProvider>? = null

    override fun getResource(packType: PackType, identifier: Identifier): IoSupplier<InputStream>? {
        if (packType != PackType.CLIENT_RESOURCES) return null

        // Fast path: only our own namespace and known-override paths need
        // provider iteration — everything else returns immediately.
        // Currently the only custom resource provider is Panorama, which
        // overrides minecraft:textures/gui/title/background/panorama_X.png.
        if (identifier.namespace != "capyclient" &&
            !identifier.path.startsWith("textures/gui/title/background/panorama")) {
            return null
        }

        val providers = cachedProviders ?: buildProviderList().also { cachedProviders = it }

        for (provider in providers) {
            try {
                val result = provider.provideResources(identifier)
                if (result != null) return result
            } catch (_: Exception) {
                // Swallow individual provider exceptions so a misbehaving
                // feature doesn't silently break vanilla texture loading.
            }
        }

        return null
    }

    private fun buildProviderList(): List<CustomResourceProvider> {
        return try {
            APIEntrypointLoader.mapEntrypoint { it.getCustomResourceProviders() }
                .flatten()
                .distinct()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun <T : Any> getMetadataSection(metadataSectionType: IndependentResourceMetadataSerializer<T>): T? = null

    override fun location(): PackLocationInfo = packInfo

    override fun getNamespaces(type: PackType): MutableSet<String> = HashSet(listOf("capyclient", "minecraft"))

    override fun close() {}

    interface CustomResourceProvider {
        fun provideResources(id: Identifier): IoSupplier<InputStream>?
    }
}