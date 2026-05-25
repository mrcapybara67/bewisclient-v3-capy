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
            downloadWithOfflineFile(Constants.API_URL + "/public_key", "public_key.pem")?.let { publicKeyBytes ->
                publicKey = publicKeyBytes.decodeToString()
            }
        }
    }

    fun processC2SPayload(payload: ServerboundCosmeticPayload, context: ServerPlayNetworking.Context) {
        val uuid = "83f0f68f-4756-43e5-ab09-85816e220225"//context.player().uuid.toString()

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
            cosmeticData = decodeCosmeticResult(result.decodeToString())
            afterLoadFunc?.invoke()
        }
    }

    fun decodeCosmeticResult(result: String): List<CosmeticEntry> {
        return result.lines().filter { it.isNotBlank() }.mapNotNull { line ->
            val path = line.substringBefore(": ").split("/")
            val type = path.firstOrNull() ?: return@mapNotNull null
            val id = path.lastOrNull() ?: return@mapNotNull null
            val params = line.substringAfter(": ").split(" ")

            return@mapNotNull CosmeticEntry(
                id = id, type = type, frames = params.firstOrNull { it.startsWith("frames=") }?.substringAfter("frames=")?.toIntOrNull() ?: 1, default = params.contains("default"), hasElytra = params.contains("elytra")
            )
        }
    }

    data class CosmeticEntry(val id: String, val type: String, val frames: Int, val default: Boolean, val hasElytra: Boolean) {
        fun getCosmetic() = CosmeticType.entries.firstOrNull { it.id == type }?.let { CosmeticIdentifier(it, id) }
    }
}