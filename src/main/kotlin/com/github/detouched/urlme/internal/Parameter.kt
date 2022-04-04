package com.github.detouched.urlme.internal

sealed class Parameter {
    data class SingleValue(val value: String) : Parameter()
    data class NamedValue(val name: String, val value: String?) : Parameter()
}
