// @VersionReplacement

package net.bewis09.capyclient.common

// @[1.21.10] ResourceLocation @[] Identifier
typealias Identifier = net.minecraft.resources./*[@]*/Identifier/*[!@]*/

// @[1.21.10] . @[] .util.
typealias Util = net.minecraft/*[@]*/.util./*[!@]*/Util

// @[1.21.11] FabricDataOutput @[] FabricPackOutput
typealias FabricDataOutput = net.fabricmc.fabric.api.datagen.v1./*[@]*/FabricDataOutput/*[!@]*/

// @[1.21.1] MetadataSectionSerializer @[] MetadataSectionType
typealias IndependentResourceMetadataSerializer<T> = net.minecraft.server.packs.metadata./*[@]*/MetadataSectionType/*[!@]*/<T>

// @[26.1] EntityType<*> @[26.1] EntityTypes<*> @[] EntityTypes
typealias EntityTypes = net.minecraft.world.entity./*[@]*/EntityType<*>/*[!@]*/