package com.github.detouched.urlme

import com.github.detouched.urlme.internal.UrlBuilder as InternalUrlBuilder

object UrlBuilder {
    fun path(): UrlMandatoryPathBuilder = InternalUrlBuilder()
}
