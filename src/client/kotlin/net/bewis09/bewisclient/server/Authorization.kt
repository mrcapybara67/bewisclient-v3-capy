package net.bewis09.bewisclient.server

import net.bewis09.bewisclient.common.catch
import net.bewis09.bewisclient.cosmetics.CommonCosmeticLoader
import net.bewis09.bewisclient.features.cosmetics.CosmeticLoader
import net.bewis09.bewisclient.generated.BuildInfo
import net.bewis09.bewisclient.settings.impl.GeneralSettings
import net.bewis09.bewisclient.util.EventEntrypoint
import net.bewis09.bewisclient.util.logic.ClientInterface
import java.math.BigInteger
import java.net.URI
import java.security.SecureRandom

object Authorization: ClientInterface, EventEntrypoint {
    var accessToken: String? = null
    val env = readRelativeFile("bewisclient", "server", ".env")?.lines()?.map { it.split("=").map { a -> a.trim() } }?.associate { it[0] to it[1] } ?: mapOf()
    var onlineModeEnabled = false

    override fun onMetadataLoaded(metadata: CommonCosmeticLoader.Metadata) {
        onlineModeEnabled = GeneralSettings.onlineMode.get()
        val clientVersion = BuildInfo.BC_VERSION
        val comparison = compareVersions(clientVersion, metadata.minimum)
        if (comparison < 0) {
            warn("Bewisclient is outdated! Minimum version: ${metadata.minimum}, your version: $clientVersion")
        }
        val startupData = if (comparison >= 0 && GeneralSettings.onlineMode.get() && GeneralSettings.acceptedEULA.get()) catch { authorize(metadata) } else null
        CosmeticLoader.loadSpecials(startupData)
    }

    fun compareVersions(clientVersion: String, serverVersion: String): Int {
        val clientParts = clientVersion.replace("-beta", ".-1").replace("-alpha", ".-2").split(".").mapNotNull { it.toIntOrNull() }
        val serverParts = serverVersion.replace("-beta", ".-1").replace("-alpha", ".-2").split(".").mapNotNull { it.toIntOrNull() }
        val maxLength = maxOf(clientParts.size, serverParts.size)
        for (i in 0 until maxLength) {
            val clientPart = clientParts.getOrElse(i) { 0 }
            val serverPart = serverParts.getOrElse(i) { 0 }
            if (clientPart != serverPart) {
                return clientPart - serverPart
            }
        }
        return 0
    }

    fun authorize(metadata: CommonCosmeticLoader.Metadata): String? {
        val uuid = env["uuid"] ?: client.gameProfile.id.toString()
        val name = env["name"] ?: client.gameProfile.name
        val accessToken = env["token"] ?: client.user.accessToken
        val random = SecureRandom()
        val serverHash = ByteArray(20)
        random.nextBytes(serverHash)
        val hashString = BigInteger(serverHash).toString(16)
        val url = "https://sessionserver.mojang.com/session/minecraft/join"
        val res = requestPost(URI(url).toURL(), """
            {
                "accessToken": "$accessToken",
                "selectedProfile": "${uuid.replace("-", "")}",
                "serverId": "$hashString"
            }
        """.trimIndent().toByteArray())
        if (res?.statusCode() != 204) {
            warn("Failed to authorize with server. Response code: ${res?.statusCode()}")
            return null
        }
        val bewisclientURL = metadata.url + "verify"
        val bewisclientRes = requestPost(URI(bewisclientURL).toURL(), """
            {
                "username": "$name",
                "serverId": "$hashString"
            }
        """.trimIndent().toByteArray())
        if (bewisclientRes?.statusCode() != 200) {
            warn("Failed to authorize with Bewisclient server. Response code: ${bewisclientRes?.statusCode()}")
            return null
        }
        Authorization.accessToken = bewisclientRes.headers().map()["Authorization"]?.firstOrNull() ?: run {
            warn("Failed to get authorization token from Bewisclient server.")
            return null
        }
        return bewisclientRes.body()?.decodeToString()
    }
}