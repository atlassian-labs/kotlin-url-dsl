package com.github.detouched.urlme

import com.github.detouched.urlme.internal.RawValue

fun Any.raw(): Any = RawValue(this)
