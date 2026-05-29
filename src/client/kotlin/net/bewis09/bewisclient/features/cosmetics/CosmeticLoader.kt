package net.bewis09.bewisclient.features.cosmetics

import com.google.gson.Gson
import com.mojang.authlib.GameProfile
import net.bewis09.bewisclient.common.catch
import net.bewis09.bewisclient.cosmetics.CommonCosmeticLoader
import net.bewis09.bewisclient.cosmetics.CommonCosmeticLoader.cosmeticData
import net.bewis09.bewisclient.cosmetics.CosmeticIdentifier
import net.bewis09.bewisclient.cosmetics.CosmeticType
import net.bewis09.bewisclient.data.Constants
import net.bewis09.bewisclient.settings.types.ObjectSetting
import net.bewis09.bewisclient.settings.types.StringMapSetting
import net.bewis09.bewisclient.util.EventEntrypoint
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.world.item.Items

object CosmeticLoader : ObjectSetting(), EventEntrypoint {
    val allowedCosmetics = mutableListOf<CosmeticIdentifier>()
    val elytraCosmetics = mutableListOf<CosmeticIdentifier>()
    val status = mutableMapOf<CosmeticIdentifier, DownloadStatus>()
    val byteData = mutableMapOf<CosmeticIdentifier, Pair<ByteArray, Int>>()
    val cosmetics = mutableMapOf<CosmeticIdentifier, Cosmetic>()
        get() {
            for (id in field.keys) {
                if (status[id] == DownloadStatus.LOADED) {
                    status[id] = DownloadStatus.REGISTER_IN_PROGRESS
                    val data = byteData[id] ?: continue
                    val cosmetic = loadCosmeticFromByteArray(id, data.first, data.second) ?: continue
                    field[id] = cosmetic
                    status[id] = DownloadStatus.COMPLETED
                }
            }
            return field
        }

    val selected = create("selected", StringMapSetting())
    val elytra = boolean("elytra", true)

    fun getStatus(identifier: CosmeticIdentifier): DownloadStatus {
        return status.getOrDefault(identifier, DownloadStatus.NOT_STARTED)
    }

    fun downloadCosmetic(cosmetic: CosmeticIdentifier, path: String, frames: Int) {
        catch {
            downloadFile(Constants.COSMETIC_URL + path) {
                status[cosmetic] = DownloadStatus.LOADED
                saveRelativeFile(it, "bewisclient", "server", path)
                byteData[cosmetic] = it to frames
            }
        } ?: run {
            status[cosmetic] = DownloadStatus.FAILED
        }
    }

    fun loadCosmetic(cosmetic: CosmeticIdentifier, frames: Int) {
        if (getStatus(cosmetic) == DownloadStatus.NOT_STARTED) {
            status[cosmetic] = DownloadStatus.IN_PROGRESS

            downloadCosmetic(cosmetic, "${cosmetic.type}/${cosmetic.id}${if (frames > 1) ".gif" else ".png"}", frames)
        }
    }

    fun loadCosmeticFromByteArray(identifier: CosmeticIdentifier, data: ByteArray, frames: Int): Cosmetic? {
        return if (frames > 1) {
            catch { AnimatedCosmetic.create(identifier, data, frames) }
        } else {
            catch { StaticCosmetic.create(identifier, data) }
        }
    }

    override fun onInitializeClient() {
        CommonCosmeticLoader.afterLoadData {
            catch {
                val uuid = client.gameProfile.id
                val specials: ByteArray? = requestWithOfflineFilePost(Constants.API_URL + "/startup", "specials.json", """{"uuid":"$uuid"}""".toByteArray())

                val specialData = catch { Gson().fromJson(specials?.decodeToString() ?: "[]", Array<SpecialEntry>::class.java) }

                cosmeticData?.forEach {
                    val identifier = it.getCosmetic() ?: return@forEach
                    if (it.default || specialData?.any { (id, type) -> id == identifier.id && type == identifier.type.id } == true) allowedCosmetics.add(identifier)
                    if (it.hasElytra) elytraCosmetics.add(identifier)
                    loadCosmetic(identifier, it.frames)
                }
            }
        }
    }

    override fun onMinecraftClientInitFinished() {
        byteData.forEach {
            if (status[it.key] == DownloadStatus.LOADED) {
                status[it.key] = DownloadStatus.REGISTER_IN_PROGRESS
                val cosmetic = loadCosmeticFromByteArray(it.key, it.value.first, it.value.second) ?: return@forEach
                cosmetics[it.key] = cosmetic
                status[it.key] = DownloadStatus.COMPLETED
            }
        }
    }

    fun getEntityBySkinTextures(hashCode: Int): PlayerInfo? {
        val playerList = client.connection?.listedOnlinePlayers ?: return null
        return playerList.firstOrNull { it.skin.hashCode() == hashCode }
    }

    data class SpecialEntry(
        val id: String,
        val type: String,
        val signature: String
    )

    fun getCosmeticForPlayer(player: GameProfile, type: CosmeticType): Cosmetic? {
        val elytraEquipped = client.level?.players()?.firstOrNull { it.gameProfile.id == player.id }?.inventory?.getItem(38)?.item == Items.ELYTRA && type == CosmeticType.CAPE
        if (player.id != client.gameProfile.id || (elytraEquipped && !this.elytra.get())) return null
        val id = CosmeticIdentifier(type, this.selected[type.id] ?: return null)
        if (id !in allowedCosmetics || (elytraEquipped && !elytraCosmetics.contains(id))) return null
        return cosmetics.getOrDefault(id, null)
    }

    enum class DownloadStatus {
        NOT_STARTED,
        IN_PROGRESS,
        LOADED,
        FAILED,
        REGISTER_IN_PROGRESS,
        COMPLETED
    }
}