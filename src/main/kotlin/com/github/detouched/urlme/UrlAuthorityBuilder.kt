package com.github.detouched.urlme

interface UrlAuthorityBuilder {
    operator fun rem(hostname: Any): UrlPostAuthorityBuilder
    operator fun rem(authority: AuthorityBuilder.() -> Authority): UrlPostAuthorityBuilder
}

interface UrlPostAuthorityBuilder : UrlMandatoryPathBuilder, UrlBuildTerminator
