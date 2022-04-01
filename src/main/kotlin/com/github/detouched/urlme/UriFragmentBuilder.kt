package com.github.detouched.urlme

@Suppress("FunctionName")
interface UriFragmentBuilder : UriBuildTerminator {
    infix fun `&`(fragmentParameter: Parameter): UriFragmentBuilder
    infix fun `&`(fragmentParameters: Iterable<Parameter>): UriFragmentBuilder
    infix fun `&`(fragmentParameters: Array<Parameter>): UriFragmentBuilder
}

@Suppress("FunctionName")
interface ConnectingUriFragmentBuilder {
    infix fun `#`(fragmentParameter: Parameter): UriFragmentBuilder
    infix fun `#`(fragmentParameters: Iterable<Parameter>): UriFragmentBuilder
    infix fun `#`(fragmentParameters: Array<Parameter>): UriFragmentBuilder
}
