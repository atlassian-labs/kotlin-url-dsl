package com.github.detouched.urlme

import java.net.URI

interface UriBuildTerminator {
    fun buildStringUri(): String
    fun buildUri(): URI
}
