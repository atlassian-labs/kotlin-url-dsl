package com.atlassian.kotlin.dsl.url

import com.atlassian.kotlin.dsl.url.internal.escape.RawValue
import java.net.InetAddress

fun Any.raw(): Any = RawValue(this)
fun String.ip(): InetAddress = InetAddress.getByName(this)
