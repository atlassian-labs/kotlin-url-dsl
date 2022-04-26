package io.github.detouched.urlme.internal.escape

import java.io.ByteArrayOutputStream

internal object Escaper {
    fun escape(value: Any, componentType: UrlComponentType): String =
        when (value) {
            is RawValue -> value.rawValue.toString()
            else -> escape(value.toString(), componentType)
        }

    private fun escape(value: String, componentType: UrlComponentType): String {
        if (value.isBlank()) return value

        val bytes = value.toByteArray()
        if (bytes.all { componentType.isAllowed(it) }) return value

        return ByteArrayOutputStream(bytes.size * 2).use { out ->
            bytes.forEach { byte ->
                if (componentType.isAllowed(byte)) {
                    out.write(byte.toInt())
                } else {
                    out.write('%'.code)
                    out.percentEncodeByte(byte)
                }
            }
            out.toString(Charsets.UTF_8.name())
        }
    }

    private fun ByteArrayOutputStream.percentEncodeByte(byte: Byte) {
        write(Character.forDigit(byte.toInt() shr 4 and 0xF, 16).uppercaseChar().code)
        write(Character.forDigit(byte.toInt()and 0xF, 16).uppercaseChar().code)
    }
}
