package com.github.detouched.urlme.internal.escape

import java.util.BitSet

internal enum class UriComponentType(private val allowedChars: BitSet) {
    PATH_SEGMENT(CharacterSet.pchar),
    NAMED_PARAMETER(CharacterSet.namedParameter),
    SINGLE_PARAMETER(CharacterSet.parameter);

    fun isAllowed(b: Byte) = b >= 0 && allowedChars.get(b.toInt())
}
