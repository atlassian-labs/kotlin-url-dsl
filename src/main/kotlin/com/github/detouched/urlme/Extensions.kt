package com.github.detouched.urlme

import com.github.detouched.urlme.internal.escape.RawValue
import java.net.InetAddress

fun Any.raw(): Any = RawValue(this)
fun String.ip(): InetAddress = InetAddress.getByName(this)
