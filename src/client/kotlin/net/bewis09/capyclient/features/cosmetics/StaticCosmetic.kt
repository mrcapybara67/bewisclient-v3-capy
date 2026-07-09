package net.bewis09.capyclient.features.cosmetics

import net.bewis09.capyclient.common.Identifier
import net.bewis09.capyclient.cosmetics.CosmeticIdentifier
import net.bewis09.capyclient.util.Bewisclient

class StaticCosmetic(private val identifier: Identifier) : Cosmetic {
    override fun getIdentifier(): Identifier {
        return identifier
    }

    companion object {
        fun create(identifier: CosmeticIdentifier, byteArray: ByteArray): StaticCosmetic {
            Bewisclient.createTexture(identifier.identifier, byteArray)
            return StaticCosmetic(identifier.identifier)
        }
    }
}