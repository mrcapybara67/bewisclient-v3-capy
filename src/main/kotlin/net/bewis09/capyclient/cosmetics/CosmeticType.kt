package net.bewis09.capyclient.cosmetics

enum class CosmeticType(val id: String) {
    HAT("hat"),
    CAPE("cape"),
    WING("wing");

    override fun toString() = id
}