package com.atlassian.kotlin.dsl.url

import java.net.URI

interface UrlBuildTerminator {
    fun buildStringUri(): String
    fun buildUri(): URI
}
