package io.github.detouched.urlme

import java.net.URI

interface UrlBuildTerminator {
    fun buildStringUri(): String
    fun buildUri(): URI
}
