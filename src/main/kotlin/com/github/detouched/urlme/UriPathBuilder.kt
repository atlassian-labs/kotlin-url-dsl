package com.github.detouched.urlme

interface UriMandatoryPathBuilder {
    operator fun div(pathSegment: Any): UriPathBuilder
    operator fun div(pathSegments: Iterable<Any>): UriPathBuilder
    operator fun div(pathSegments: Array<Any>): UriPathBuilder
}

@Suppress("FunctionName", "DANGEROUS_CHARACTERS")
interface UriPathBuilder : UriMandatoryPathBuilder, ConnectingUriFragmentBuilder, UriBuildTerminator {
    infix fun `?`(queryParameter: Parameter): UriQueryBuilder
    infix fun `?`(queryParameters: Iterable<Parameter>): UriQueryBuilder
    infix fun `?`(queryParameters: Array<Parameter>): UriQueryBuilder
}
