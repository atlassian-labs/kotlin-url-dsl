package com.github.detouched.urlme

import com.github.detouched.urlme.internal.InternalUrlBuilder

object UrlBuilder : UrlMandatoryPathBuilder {
    // Relative URL builders
    override fun div(pathSegment: Any): UrlPathBuilder = InternalUrlBuilder() / pathSegment
    override fun div(pathSegments: Iterable<Any>): UrlPathBuilder = InternalUrlBuilder() / pathSegments

    operator fun times(scheme: String): UrlAuthorityBuilder = InternalUrlBuilder(scheme = scheme)
}
