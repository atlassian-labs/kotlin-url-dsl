package com.github.detouched.urlme

internal class RawValue(value: Any)

fun Any.raw(): Any = RawValue(this)
