package net.bewis09.capyclient.cosmetics

import net.bewis09.capyclient.common.createIdentifier

data class CosmeticIdentifier(val type: CosmeticType, val id: String) {
    val identifier = createIdentifier("capyclient", "cosmetics/${type.id}/$id")
}