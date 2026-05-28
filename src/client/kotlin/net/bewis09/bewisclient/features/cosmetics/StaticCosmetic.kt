package net.bewis09.bewisclient.features.cosmetics

import net.bewis09.bewisclient.common.Identifier
import net.bewis09.bewisclient.cosmetics.CosmeticIdentifier
import net.bewis09.bewisclient.util.Bewisclient

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