package com.github.detouched.urlme

typealias Parameter = Pair<Any, Any?>

@Suppress("FunctionName")
interface UriQueryBuilder : ConnectingUriFragmentBuilder, UriBuildTerminator {
    infix fun `&`(queryParameter: Parameter): UriQueryBuilder
    infix fun `&`(queryParameters: Iterable<Parameter>): UriQueryBuilder
    infix fun `&`(queryParameters: Array<Parameter>): UriQueryBuilder
}
