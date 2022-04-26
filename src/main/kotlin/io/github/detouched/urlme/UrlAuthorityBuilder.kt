package io.github.detouched.urlme

import io.github.detouched.urlme.internal.InternalAuthorityBuilder
import io.github.detouched.urlme.internal.InternalUrlBuilder

object UrlAuthorityBuilder {
    operator fun String.rem(hostname: Any): UrlPostAuthorityBuilder =
        InternalUrlBuilder(scheme = this, authority = InternalAuthorityBuilder(host = hostname))
    operator fun String.rem(buildAuthority: AuthorityBuilder.() -> Authority): UrlPostAuthorityBuilder =
        InternalUrlBuilder(scheme = this, authority = InternalAuthorityBuilder().buildAuthority())
}

interface UrlPostAuthorityBuilder : UrlMandatoryPathBuilder, UrlBuildTerminator
