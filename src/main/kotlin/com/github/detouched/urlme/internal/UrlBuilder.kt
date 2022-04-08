package com.github.detouched.urlme.internal

import com.github.detouched.urlme.NamedValueParameter
import com.github.detouched.urlme.UrlBuildTerminator
import com.github.detouched.urlme.UrlFragmentBuilder
import com.github.detouched.urlme.UrlPathBuilder
import com.github.detouched.urlme.UrlQueryBuilder
import com.github.detouched.urlme.internal.escape.Escaper
import com.github.detouched.urlme.internal.escape.UrlComponentType.NAMED_PARAMETER
import com.github.detouched.urlme.internal.escape.UrlComponentType.PATH_SEGMENT
import com.github.detouched.urlme.internal.escape.UrlComponentType.SINGLE_PARAMETER
import java.net.URI

@Suppress("DANGEROUS_CHARACTERS")
internal data class UrlBuilder(
    val pathSegments: List<Any> = emptyList(),
    val queryParameters: List<Parameter> = emptyList(),
    val fragmentParameters: List<Parameter> = emptyList(),
) : UrlPathBuilder, UrlQueryBuilder, UrlBuildTerminator {
    override fun div(pathSegment: Any): UrlPathBuilder =
        div(listOf(pathSegment))

    override fun div(pathSegments: Iterable<Any>): UrlPathBuilder =
        copy(pathSegments = this@UrlBuilder.pathSegments + pathSegments)

    override fun `?`(queryParameter: Any): UrlQueryBuilder {
        require(queryParameter.toString().isNotBlank()) { "Query parameter must not be blank" }
        return copy(queryParameters = this@UrlBuilder.queryParameters + Parameter.SingleValue(queryParameter))
    }

    override fun `?`(queryParameter: NamedValueParameter): UrlQueryBuilder =
        `?`(listOf(queryParameter))

    override fun `?`(queryParameters: Iterable<NamedValueParameter>): UrlQueryBuilder =
        copy(
            queryParameters = this@UrlBuilder.queryParameters + queryParameters.map { (name, value) ->
                require(name.toString().isNotBlank()) { "Query parameter name must not be blank" }
                Parameter.NamedValue(name, value)
            }
        )

    override fun `&`(queryParameter: Any): UrlQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameter: NamedValueParameter): UrlQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameters: Iterable<NamedValueParameter>): UrlQueryBuilder =
        `?`(queryParameters)

    override fun `#`(fragmentParameter: Any): UrlFragmentBuilder {
        require(fragmentParameter.toString().isNotBlank()) { "Fragment parameter must not be blank" }
        return copy(fragmentParameters = this@UrlBuilder.fragmentParameters + Parameter.SingleValue(fragmentParameter))
            .fragmentBuildingView()
    }

    override fun `#`(fragmentParameter: NamedValueParameter): UrlFragmentBuilder =
        `#`(listOf(fragmentParameter))

    override fun `#`(fragmentParameters: Iterable<NamedValueParameter>): UrlFragmentBuilder =
        copy(
            fragmentParameters = this@UrlBuilder.fragmentParameters + fragmentParameters.map { (name, value) ->
                require(name.toString().isNotBlank()) { "Fragment parameter name must not be blank" }
                Parameter.NamedValue(name, value)
            }
        )
            .fragmentBuildingView()

    override fun buildStringUri(): String {
        fun StringBuilder.appendParameters(
            parameters: List<Parameter>,
            sectionDelimiter: Char,
            parametersDelimiter: Char,
            escapingFunction: (String) -> String,
        ) {
            if (parameters.isNotEmpty()) {
                append(sectionDelimiter)
                parameters.forEachIndexed { index, parameter ->
                    when (parameter) {
                        is Parameter.SingleValue -> append(escape(parameter.value, escapingFunction))
                        is Parameter.NamedValue -> {
                            val name = escape(parameter.name, escapingFunction)
                            val value = parameter.value?.let { escape(it, escapingFunction) } ?: ""
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
            pathSegments.forEach { segment ->
                append('/')
                append(escape(segment) { Escaper.escape(it, PATH_SEGMENT) })
            }
            appendParameters(queryParameters, '?', '&') { Escaper.escape(it, NAMED_PARAMETER) }
            appendParameters(fragmentParameters, '#', '&') {
                val type = when (fragmentParameters.singleOrNull()) {
                    is Parameter.SingleValue -> SINGLE_PARAMETER
                    else -> NAMED_PARAMETER
                }
                Escaper.escape(it, type)
            }
        }
    }

    override fun buildUri(): URI = URI(buildStringUri())

    private fun escape(value: Any, escapingFunction: (String) -> String): String =
        when (value) {
            is RawValue -> value.rawValue.toString()
            else -> escapingFunction(value.toString())
        }

    private fun fragmentBuildingView() = FragmentBuildingUrlBuilderView(this)
}

internal class FragmentBuildingUrlBuilderView(
    private val urlBuilder: UrlBuilder,
) : UrlFragmentBuilder, UrlBuildTerminator {
    override fun `&`(fragmentParameter: Any) = urlBuilder `#` fragmentParameter
    override fun `&`(fragmentParameter: NamedValueParameter) = urlBuilder `#` fragmentParameter
    override fun `&`(fragmentParameters: Iterable<NamedValueParameter>) = urlBuilder `#` fragmentParameters
    override fun buildStringUri() = urlBuilder.buildStringUri()
    override fun buildUri() = urlBuilder.buildUri()
}
