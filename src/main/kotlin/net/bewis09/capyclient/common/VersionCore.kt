// @VersionReplacement

package net.bewis09.capyclient.common

import net.minecraft.WorldVersion
import net.minecraft.resources.ResourceKey

val WorldVersion.name: String
    // @[1.21.5] name @[] name()
    get() = this./*[@]*/name()/*[!@]*/

// @[1.21.10] isAllowedInResourceLocation @[] isAllowedInIdentifier
fun isAllowedInIdentifier(char: Char) = Identifier./*[@]*/isAllowedInIdentifier/*[!@]*/(char)

// @[1.21.10] location() @[] identifier()
fun <T: Any> ResourceKey<T>.id(): Identifier = this./*[@]*/identifier()/*[!@]*/