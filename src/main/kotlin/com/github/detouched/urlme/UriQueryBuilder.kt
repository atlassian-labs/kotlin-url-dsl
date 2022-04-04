package com.github.detouched.urlme

@Suppress("FunctionName")
interface UriQueryBuilder : ConnectingUriFragmentBuilder, UriBuildTerminator {
    infix fun `&`(queryParameter: Any): UriQueryBuilder
    infix fun `&`(queryParameter: NamedValueParameter): UriQueryBuilder
    infix fun `&`(queryParameters: Iterable<NamedValueParameter>): UriQueryBuilder
}
