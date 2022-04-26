package io.github.detouched.urlme.internal.escape

import java.util.BitSet

internal enum class UrlComponentType(private val allowedChars: BitSet) {
    USER_INFO_PART(CharacterSet.userInfoPath),
    HOST(CharacterSet.host),
    PATH_SEGMENT(CharacterSet.pchar),
    NAMED_PARAMETER(CharacterSet.namedParameter),
    SINGLE_PARAMETER(CharacterSet.parameter);

    fun isAllowed(b: Byte) = b >= 0 && allowedChars.get(b.toInt())
}
