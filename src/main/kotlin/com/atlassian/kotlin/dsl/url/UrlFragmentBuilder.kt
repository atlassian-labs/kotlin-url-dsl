package com.atlassian.kotlin.dsl.url

@Suppress("FunctionName")
interface UrlFragmentBuilder : UrlBuildTerminator {
    infix fun `&`(fragmentParameter: Any): UrlFragmentBuilder
    infix fun `&`(fragmentParameter: NamedValueParameter): UrlFragmentBuilder
    infix fun `&`(fragmentParameters: Iterable<NamedValueParameter>): UrlFragmentBuilder
}

@Suppress("FunctionName")
interface ConnectingUrlFragmentBuilder {
    infix fun `#`(fragmentParameter: Any): UrlFragmentBuilder
    infix fun `#`(fragmentParameter: NamedValueParameter): UrlFragmentBuilder
    infix fun `#`(fragmentParameters: Iterable<NamedValueParameter>): UrlFragmentBuilder
}
