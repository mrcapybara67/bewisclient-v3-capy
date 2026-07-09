package net.bewis09.capyclient.server

import net.bewis09.capyclient.common.catch
import net.bewis09.capyclient.cosmetics.CommonCosmeticLoader
import net.bewis09.capyclient.features.cosmetics.CosmeticLoader
import net.bewis09.capyclient.generated.BuildInfo
import net.bewis09.capyclient.features.sidebar.General
import net.bewis09.capyclient.util.EventEntrypoint
import net.bewis09.capyclient.util.logic.ClientInterface
import java.math.BigInteger
import java.net.URI
import java.security.SecureRandom

object Authorization: ClientInterface, EventEntrypoint {
    var accessToken: String? = null
    val env = readRelativeFile("capyclient", "server", ".env")?.lines()?.map { it.split("=").map { a -> a.trim() } }?.associate { it[0] to it[1] } ?: mapOf()
    var onlineModeEnabled = false

    override fun onMetadataLoaded(metadata: CommonCosmeticLoader.Metadata) {
        onlineModeEnabled = General.onlineMode.get()
        val clientVersion = BuildInfo.BC_VERSION
        val comparison = compareVersions(clientVersion, metadata.minimum)
        if (comparison < 0) {
            warn("Capy Client is outdated! Minimum version: ${metadata.minimum}, your version: $clientVersion")
        }
        val startupData = if (comparison >= 0 && General.onlineMode.get() && General.acceptedEULA.get()) catch { authorize(metadata) } else null
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
        val capyclientURL = metadata.url + "verify"
        val capyclientRes = requestPost(URI(capyclientURL).toURL(), """
            {
                "username": "$name",
                "serverId": "$hashString"
            }
        """.trimIndent().toByteArray())
        if (capyclientRes?.statusCode() != 200) {
            warn("Failed to authorize with Capy Client server. Response code: ${capyclientRes?.statusCode()}")
            return null
        }
        Authorization.accessToken = capyclientRes.headers().map()["Authorization"]?.firstOrNull() ?: run {
            warn("Failed to get authorization token from Capy Client server.")
            return null
        }
        return capyclientRes.body()?.decodeToString()
    }
}