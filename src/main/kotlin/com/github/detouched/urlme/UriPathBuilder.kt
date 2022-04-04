package com.github.detouched.urlme

interface UriMandatoryPathBuilder {
    operator fun div(pathSegment: Any): UriPathBuilder
    operator fun div(pathSegments: Iterable<Any>): UriPathBuilder
}

@Suppress("FunctionName", "DANGEROUS_CHARACTERS")
interface UriPathBuilder : UriMandatoryPathBuilder, ConnectingUriFragmentBuilder, UriBuildTerminator {
    infix fun `?`(queryParameter: Any): UriQueryBuilder
    infix fun `?`(queryParameter: NamedValueParameter): UriQueryBuilder
    infix fun `?`(queryParameters: Iterable<NamedValueParameter>): UriQueryBuilder
}
