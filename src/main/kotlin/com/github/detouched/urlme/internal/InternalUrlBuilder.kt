package com.github.detouched.urlme.internal

import com.github.detouched.urlme.Authority
import com.github.detouched.urlme.NamedValueParameter
import com.github.detouched.urlme.UrlBuildTerminator
import com.github.detouched.urlme.UrlFragmentBuilder
import com.github.detouched.urlme.UrlPathBuilder
import com.github.detouched.urlme.UrlPostAuthorityBuilder
import com.github.detouched.urlme.UrlQueryBuilder
import com.github.detouched.urlme.internal.escape.Escaper
import com.github.detouched.urlme.internal.escape.UrlComponentType
import com.github.detouched.urlme.internal.escape.UrlComponentType.NAMED_PARAMETER
import com.github.detouched.urlme.internal.escape.UrlComponentType.PATH_SEGMENT
import com.github.detouched.urlme.internal.escape.UrlComponentType.SINGLE_PARAMETER
import com.github.detouched.urlme.raw
import java.net.URI

private val schemeRegex = Regex("[a-zA-Z][a-zA-Z0-9+-.]*")

@Suppress("DANGEROUS_CHARACTERS")
internal data class InternalUrlBuilder(
    private val scheme: String? = null,
    private val authority: Authority? = null,
    private val pathSegments: List<Any> = emptyList(),
    private val queryParameters: List<Parameter> = emptyList(),
    private val fragmentParameters: List<Parameter> = emptyList(),
) : UrlPostAuthorityBuilder, UrlPathBuilder, UrlQueryBuilder, UrlBuildTerminator {
    constructor(
        baseUri: URI,
        preservePath: Boolean,
        preserveQuery: Boolean,
        preserveFragment: Boolean,
    ) : this(
        baseUri.scheme,
        baseUri.authority?.let { Authority { it } },
        baseUri.rawPath
            ?.takeIf { preservePath }
            ?.takeUnless { it.isEmpty() }
            ?.trimStart('/')
            ?.split('/')
            ?.map { it.raw() }
            ?: emptyList(),
        baseUri.rawQuery
            ?.takeIf { preserveQuery }
            .parseParameters(),
        baseUri.rawFragment
            ?.takeIf { preserveFragment }
            .parseParameters(),
    )

    init {
        if (scheme != null) require(scheme.matches(schemeRegex)) {
            "Scheme must start with a letter and contain only letters, digits, dots, plus and minus signs"
        }
        queryParameters.validate("Query")
        fragmentParameters.validate("Fragment")
    }

    override fun div(pathSegment: Any): UrlPathBuilder =
        div(listOf(pathSegment))

    override fun div(pathSegments: Iterable<Any>): UrlPathBuilder =
        copy(pathSegments = this@InternalUrlBuilder.pathSegments + pathSegments)

    override fun `?`(queryParameter: Any): UrlQueryBuilder =
        copy(queryParameters = this@InternalUrlBuilder.queryParameters + Parameter.SingleValue(queryParameter))

    override fun `?`(queryParameter: NamedValueParameter): UrlQueryBuilder =
        `?`(listOf(queryParameter))

    override fun `?`(queryParameters: Iterable<NamedValueParameter>): UrlQueryBuilder =
        copy(
            queryParameters = this@InternalUrlBuilder.queryParameters +
                queryParameters.map { (name, value) -> Parameter.NamedValue(name, value) }
        )

    override fun `&`(queryParameter: Any): UrlQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameter: NamedValueParameter): UrlQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameters: Iterable<NamedValueParameter>): UrlQueryBuilder =
        `?`(queryParameters)

    override fun `#`(fragmentParameter: Any): UrlFragmentBuilder {
        return copy(
            fragmentParameters = this@InternalUrlBuilder.fragmentParameters +
                Parameter.SingleValue(fragmentParameter)
        )
            .fragmentBuildingView()
    }

    override fun `#`(fragmentParameter: NamedValueParameter): UrlFragmentBuilder =
        `#`(listOf(fragmentParameter))

    override fun `#`(fragmentParameters: Iterable<NamedValueParameter>): UrlFragmentBuilder =
        copy(
            fragmentParameters = this@InternalUrlBuilder.fragmentParameters +
                fragmentParameters.map { (name, value) -> Parameter.NamedValue(name, value) }
        )
            .fragmentBuildingView()

    override fun buildStringUri(): String {
        fun StringBuilder.appendParameters(
            parameters: List<Parameter>,
            sectionDelimiter: Char,
            parametersDelimiter: Char,
            componentType: UrlComponentType,
        ) {
            if (parameters.isNotEmpty()) {
                append(sectionDelimiter)
                parameters.forEachIndexed { index, parameter ->
                    when (parameter) {
                        is Parameter.SingleValue -> append(Escaper.escape(parameter.value, componentType))
                        is Parameter.NamedValue -> {
                            val name = Escaper.escape(parameter.name, componentType)
                            val value = parameter.value?.let { Escaper.escape(it, componentType) } ?: ""
                            append("$name=$value")
                        }
                    }
                    if (index < parameters.size - 1) {
                        append(parametersDelimiter)
                    }
                }
            }
        }

        return buildString {
            scheme?.let {
                append(it)
                append(':')
            }
            authority?.let {
                append("//")
                append(authority.buildString())
            }
            pathSegments.forEach { segment ->
                append('/')
                append(Escaper.escape(segment, PATH_SEGMENT))
            }
            appendParameters(queryParameters, '?', '&', NAMED_PARAMETER)
            appendParameters(
                fragmentParameters, '#', '&',
                when (fragmentParameters.singleOrNull()) {
                    is Parameter.SingleValue -> SINGLE_PARAMETER
                    else -> NAMED_PARAMETER
                }
            )
        }
    }

    override fun buildUri(): URI = URI(buildStringUri())

    private fun fragmentBuildingView() = FragmentBuildingUrlBuilderView(this)

    private fun List<Parameter>.validate(type: String) {
        forEach { parameter ->
            when (parameter) {
                is Parameter.SingleValue ->
                    require(parameter.value.toString().isNotBlank()) { "$type parameter must not be blank" }
                is Parameter.NamedValue ->
                    require(parameter.name.toString().isNotBlank()) { "$type parameter name must not be blank" }
            }
        }
    }
}

internal class FragmentBuildingUrlBuilderView(
    private val urlBuilder: InternalUrlBuilder,
) : UrlFragmentBuilder, UrlBuildTerminator {
    override fun `&`(fragmentParameter: Any) = urlBuilder `#` fragmentParameter
    override fun `&`(fragmentParameter: NamedValueParameter) = urlBuilder `#` fragmentParameter
    override fun `&`(fragmentParameters: Iterable<NamedValueParameter>) = urlBuilder `#` fragmentParameters
    override fun buildStringUri() = urlBuilder.buildStringUri()
    override fun buildUri() = urlBuilder.buildUri()
}

private fun String?.parseParameters(): List<Parameter> =
    this?.takeUnless { it.isEmpty() }
        ?.split('&')
        ?.filter { it.isNotEmpty() }
        ?.map { Parameter.SingleValue(it.raw()) }
        ?: emptyList()
