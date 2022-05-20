package com.atlassian.kotlin.dsl.url

@Suppress("FunctionName")
interface UrlQueryBuilder : ConnectingUrlFragmentBuilder, UrlBuildTerminator {
    infix fun `&`(queryParameter: Any): UrlQueryBuilder
    infix fun `&`(queryParameter: NamedValueParameter): UrlQueryBuilder
    infix fun `&`(queryParameters: Iterable<NamedValueParameter>): UrlQueryBuilder
}
