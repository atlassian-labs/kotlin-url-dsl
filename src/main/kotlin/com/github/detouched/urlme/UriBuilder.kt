package com.github.detouched.urlme

import com.github.detouched.urlme.internal.UriBuilder as InternalUriBuilder

object UriBuilder {
    fun path(): UriMandatoryPathBuilder = InternalUriBuilder()
}
