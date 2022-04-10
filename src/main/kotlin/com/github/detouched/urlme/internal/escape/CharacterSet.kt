package com.github.detouched.urlme.internal.escape

import java.util.BitSet

internal object CharacterSet {
    val alpha = bitset('a'..'z') + bitset('A'..'Z')
    val digits = bitset('0'..'9')
    val genericDelimiters = bitset(':', '/', '?', '#', '[', ']', '@')
    val subDelimiters = bitset('!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=')
    val reserved = genericDelimiters + subDelimiters
    val unreserved = alpha + digits + bitset('-', '.', '_', '~')
    val pchar = unreserved + subDelimiters + bitset(':', '@')

    val userInfoPath = unreserved + subDelimiters
    val host = unreserved + subDelimiters
    val parameter = pchar + bitset('/', '?')
    val namedParameter = parameter - bitset('=', '&')

    private fun bitset(vararg chars: Char): BitSet = bitset(chars.asIterable())
    private fun bitset(chars: CharRange): BitSet = bitset(chars.asIterable())

    private fun bitset(chars: Iterable<Char>): BitSet =
        BitSet().apply {
            chars.forEach { set(it.code) }
        }

    private operator fun BitSet.plus(other: BitSet) =
        BitSet().apply {
            or(this@plus)
            or(other)
        }

    private operator fun BitSet.minus(other: BitSet) =
        BitSet().apply {
            or(this@minus)
            andNot(other)
        }
}
