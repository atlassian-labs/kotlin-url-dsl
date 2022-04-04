package com.github.detouched.urlme

@Suppress("FunctionName")
interface UriFragmentBuilder : UriBuildTerminator {
    infix fun `&`(fragmentParameter: Any): UriFragmentBuilder
    infix fun `&`(fragmentParameter: NamedValueParameter): UriFragmentBuilder
    infix fun `&`(fragmentParameters: Iterable<NamedValueParameter>): UriFragmentBuilder
}

@Suppress("FunctionName")
interface ConnectingUriFragmentBuilder {
    infix fun `#`(fragmentParameter: Any): UriFragmentBuilder
    infix fun `#`(fragmentParameter: NamedValueParameter): UriFragmentBuilder
    infix fun `#`(fragmentParameters: Iterable<NamedValueParameter>): UriFragmentBuilder
}
