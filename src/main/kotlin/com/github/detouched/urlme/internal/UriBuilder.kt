package com.github.detouched.urlme.internal

import com.github.detouched.urlme.NamedValueParameter
import com.github.detouched.urlme.UriBuildTerminator
import com.github.detouched.urlme.UriFragmentBuilder
import com.github.detouched.urlme.UriPathBuilder
import com.github.detouched.urlme.UriQueryBuilder
import com.github.detouched.urlme.internal.escape.Escaper
import com.github.detouched.urlme.internal.escape.UriComponentType.NAMED_PARAMETER
import com.github.detouched.urlme.internal.escape.UriComponentType.PATH_SEGMENT
import com.github.detouched.urlme.internal.escape.UriComponentType.SINGLE_PARAMETER
import java.net.URI

@Suppress("DANGEROUS_CHARACTERS")
internal data class UriBuilder(
    val pathSegments: List<Any> = emptyList(),
    val queryParameters: List<Parameter> = emptyList(),
    val fragmentParameters: List<Parameter> = emptyList(),
) : UriPathBuilder, UriQueryBuilder, UriBuildTerminator {
    override fun div(pathSegment: Any): UriPathBuilder =
        div(listOf(pathSegment))

    override fun div(pathSegments: Iterable<Any>): UriPathBuilder =
        copy(pathSegments = this@UriBuilder.pathSegments + pathSegments)

    override fun `?`(queryParameter: Any): UriQueryBuilder =
        copy(queryParameters = this@UriBuilder.queryParameters + Parameter.SingleValue(queryParameter))

    override fun `?`(queryParameter: NamedValueParameter): UriQueryBuilder =
        `?`(listOf(queryParameter))

    override fun `?`(queryParameters: Iterable<NamedValueParameter>): UriQueryBuilder =
        copy(
            queryParameters = this@UriBuilder.queryParameters + queryParameters.map { (name, value) ->
                Parameter.NamedValue(name, value)
            }
        )

    override fun `&`(queryParameter: Any): UriQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameter: NamedValueParameter): UriQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameters: Iterable<NamedValueParameter>): UriQueryBuilder =
        `?`(queryParameters)

    override fun `#`(fragmentParameter: Any): UriFragmentBuilder =
        copy(fragmentParameters = this@UriBuilder.fragmentParameters + Parameter.SingleValue(fragmentParameter))
            .fragmentBuildingView()

    override fun `#`(fragmentParameter: NamedValueParameter): UriFragmentBuilder =
        `#`(listOf(fragmentParameter))

    override fun `#`(fragmentParameters: Iterable<NamedValueParameter>): UriFragmentBuilder =
        copy(
            fragmentParameters = this@UriBuilder.fragmentParameters + fragmentParameters.map { (name, value) ->
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
                val type = if (fragmentParameters.size > 1) NAMED_PARAMETER else SINGLE_PARAMETER
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

    private fun fragmentBuildingView() = FragmentBuildingUriBuilderView(this)
}

internal class FragmentBuildingUriBuilderView(
    private val uriBuilder: UriBuilder,
) : UriFragmentBuilder, UriBuildTerminator {
    override fun `&`(fragmentParameter: Any) = uriBuilder `#` fragmentParameter
    override fun `&`(fragmentParameter: NamedValueParameter) = uriBuilder `#` fragmentParameter
    override fun `&`(fragmentParameters: Iterable<NamedValueParameter>) = uriBuilder `#` fragmentParameters
    override fun buildStringUri() = uriBuilder.buildStringUri()
    override fun buildUri() = uriBuilder.buildUri()
}
