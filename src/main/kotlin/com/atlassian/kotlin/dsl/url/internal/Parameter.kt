package com.atlassian.kotlin.dsl.url.internal

sealed class Parameter {
    data class SingleValue(val value: Any) : Parameter()
    data class NamedValue(val name: Any, val value: Any?) : Parameter()
}
