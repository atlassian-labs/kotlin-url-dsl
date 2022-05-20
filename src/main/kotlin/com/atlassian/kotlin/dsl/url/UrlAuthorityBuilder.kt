package com.atlassian.kotlin.dsl.url

import com.atlassian.kotlin.dsl.url.internal.InternalAuthorityBuilder
import com.atlassian.kotlin.dsl.url.internal.InternalUrlBuilder

object UrlAuthorityBuilder {
    operator fun String.rem(hostname: Any): UrlPostAuthorityBuilder =
        InternalUrlBuilder(scheme = this, authority = InternalAuthorityBuilder(host = hostname))
    operator fun String.rem(buildAuthority: AuthorityBuilder.() -> Authority): UrlPostAuthorityBuilder =
        InternalUrlBuilder(scheme = this, authority = InternalAuthorityBuilder().buildAuthority())
}

interface UrlPostAuthorityBuilder : UrlMandatoryPathBuilder, UrlBuildTerminator
