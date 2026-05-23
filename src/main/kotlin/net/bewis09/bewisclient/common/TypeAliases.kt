// @VersionReplacement

package net.bewis09.bewisclient.common

// @[1.21.10] ResourceLocation @[] Identifier
typealias Identifier = net.minecraft.resources./*[@]*/ResourceLocation/*[!@]*/

// @[1.21.10] . @[] .util.
typealias Util = net.minecraft/*[@]*/./*[!@]*/Util

// @[1.21.11] FabricDataOutput @[] FabricPackOutput
typealias FabricDataOutput = net.fabricmc.fabric.api.datagen.v1./*[@]*/FabricDataOutput/*[!@]*/

// @[1.21.1] MetadataSectionSerializer @[] MetadataSectionType
typealias IndependentResourceMetadataSerializer<T> = net.minecraft.server.packs.metadata./*[@]*/MetadataSectionSerializer/*[!@]*/<T>

// @[26.1] EntityType<*> @[26.1] EntityTypes<*> @[] EntityTypes
typealias EntityTypes = net.minecraft.world.entity./*[@]*/EntityType<*>/*[!@]*/