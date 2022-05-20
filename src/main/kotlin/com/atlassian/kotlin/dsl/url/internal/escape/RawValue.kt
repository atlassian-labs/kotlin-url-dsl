package com.atlassian.kotlin.dsl.url.internal.escape

internal class RawValue(val rawValue: Any) {
    override fun toString() = rawValue.toString()
}
