package com.github.detouched.urlme

interface UrlMandatoryPathBuilder {
    operator fun div(pathSegment: Any): UrlPathBuilder
    operator fun div(pathSegments: Iterable<Any>): UrlPathBuilder
}

@Suppress("FunctionName", "DANGEROUS_CHARACTERS")
interface UrlPathBuilder : UrlMandatoryPathBuilder, ConnectingUrlFragmentBuilder, UrlBuildTerminator {
    infix fun `?`(queryParameter: Any): UrlQueryBuilder
    infix fun `?`(queryParameter: NamedValueParameter): UrlQueryBuilder
    infix fun `?`(queryParameters: Iterable<NamedValueParameter>): UrlQueryBuilder
}
