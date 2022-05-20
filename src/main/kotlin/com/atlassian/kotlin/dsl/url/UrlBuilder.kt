package com.atlassian.kotlin.dsl.url

import com.atlassian.kotlin.dsl.url.internal.InternalUrlBuilder
import java.net.URI

object UrlBuilder : UrlMandatoryPathBuilder {
    // Relative URL builders
    override fun div(pathSegment: Any): UrlPathBuilder = InternalUrlBuilder() / pathSegment
    override fun div(pathSegments: Iterable<Any>): UrlPathBuilder = InternalUrlBuilder() / pathSegments

    // Absolute URL builder
    operator fun invoke(build: UrlAuthorityBuilder.() -> UrlBuildTerminator): UrlBuildTerminator =
        UrlAuthorityBuilder.build()

    // Builders with base URLs
    operator fun invoke(
        baseUrl: String,
        preservePath: Boolean = true,
        preserveQuery: Boolean = true,
        preserveFragment: Boolean = true,
    ): UrlPathBuilder = invoke(URI.create(baseUrl), preservePath, preserveQuery, preserveFragment)
    operator fun invoke(
        baseUrl: URI,
        preservePath: Boolean = true,
        preserveQuery: Boolean = true,
        preserveFragment: Boolean = true,
    ): UrlPathBuilder = InternalUrlBuilder(baseUrl, preservePath, preserveQuery, preserveFragment)
}
