package net.bewis09.bewisclient.cosmetics

import net.bewis09.bewisclient.common.Util
import net.bewis09.bewisclient.common.logic.ServerInterface
import net.bewis09.bewisclient.data.Constants
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

object CommonCosmeticLoader : ServerInterface {
    var cosmeticData: List<CosmeticEntry>? = null
    var metadata: Metadata? = null
    var afterLoadFunc: (() -> Unit)? = null
    var publicKey: String? = null

    fun afterLoadData(func: () -> Unit) {
        if (cosmeticData != null) {
            Util.nonCriticalIoPool().execute { func() }
        } else {
            afterLoadFunc = func
        }
    }

    fun loadPublicKey() {
        Util.nonCriticalIoPool().execute {
            downloadWithOfflineFile((metadata?.url ?: return@execute) + "public_key", "public_key.pem")?.let { publicKeyBytes ->
                publicKey = publicKeyBytes.decodeToString()
            }
        }
    }

    fun processC2SPayload(payload: ServerboundCosmeticPayload, context: ServerPlayNetworking.Context) {
        val uuid = context.player().uuid.toString()

        payload.cosmetics.forEach {
            val type = it.key
            val id = it.value.first
            val signature = it.value.second

            val content = "$uuid/$type/$id"

            val verified = publicKey?.run { verify(content, signature, this) } ?: true

            if (verified) {
                info("Verified cosmetic for player ${context.player().name.string}: $type/$id")
            } else {
                warn("Failed to verify cosmetic for player ${context.player().name.string}: $type/$id")
            }
        }
    }

    fun verify(payload: String, signatureBase64: String, publicKeyPem: String): Boolean {
        val base64Key = publicKeyPem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")

        val publicKey = KeyFactory.getInstance("RSA")
            .generatePublic(X509EncodedKeySpec(Base64.getDecoder().decode(base64Key)))

        val signatureBytes = Base64.getDecoder().decode(signatureBase64)

        return Signature.getInstance("SHA256withRSA").run {
            initVerify(publicKey)
            update(payload.toByteArray(Charsets.UTF_8))
            verify(signatureBytes)
        }
    }

    fun loadCosmeticData() {
        Util.nonCriticalIoPool().execute {
            val result: ByteArray = downloadWithOfflineFile(Constants.COSMETIC_URL + "/cosmetics.txt", "cosmetics.txt") ?: return@execute
            val data = decodeCosmeticResult(result.decodeToString()) ?: return@execute
            cosmeticData = data.cosmeticData
            metadata = data.metadata
            afterLoadFunc?.invoke()
        }
    }

    fun decodeCosmeticResult(result: String): DecodedResult? {
        val lines = result.lines().filter { it.isNotBlank() }
        val dataParts = mutableMapOf<String, MutableList<String>>()

        var currentPart: String? = null
        var currentData = mutableListOf<String>()
        lines.forEach {
            if (it.startsWith("[") && it.endsWith("]")) {
                if (currentPart != null) {
                    dataParts[currentPart] = currentData
                }
                currentPart = it.substring(1, it.length - 1)
                currentData = mutableListOf()
            } else if (currentPart != null) {
                currentData.add(it)
            }
        }

        dataParts[currentPart ?: return null] = currentData

        val cosmetics = dataParts["Cosmetics"] ?: return null
        val metadata = dataParts["Metadata"] ?: return null

        return DecodedResult(cosmetics.mapNotNull { line ->
            val path = line.substringBefore(": ").split("/")
            val type = path.firstOrNull() ?: return@mapNotNull null
            val id = path.lastOrNull() ?: return@mapNotNull null
            val params = line.substringAfter(": ").split(" ")

            return@mapNotNull CosmeticEntry(
                id = id,
                type = type,
                frames = params.firstOrNull { it.startsWith("frames=") }?.substringAfter("frames=")?.toIntOrNull() ?: 1,
                default = params.contains("default"),
                hasElytra = params.contains("elytra"),
                category = params.firstOrNull { it.startsWith("category=") }?.substringAfter("category="),
            )
        }, Metadata(
            minimum = metadata.firstOrNull { it.startsWith("minimum=") }?.substringAfter("minimum=") ?: return null,
            url = metadata.firstOrNull { it.startsWith("url=") }?.substringAfter("url=") ?: return null
        ))
    }

    class DecodedResult(val cosmeticData: List<CosmeticEntry>, val metadata: Metadata)

    data class Metadata(val minimum: String, val url: String)

    data class CosmeticEntry(val id: String, val type: String, val frames: Int, val default: Boolean, val hasElytra: Boolean, val category: String?) {
        fun getCosmetic() = CosmeticType.entries.firstOrNull { it.id == type }?.let { CosmeticIdentifier(it, id) }
    }
}