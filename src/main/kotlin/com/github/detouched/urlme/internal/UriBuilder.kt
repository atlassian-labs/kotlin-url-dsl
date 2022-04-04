package com.github.detouched.urlme.internal

import com.github.detouched.urlme.NamedValueParameter
import com.github.detouched.urlme.UriBuildTerminator
import com.github.detouched.urlme.UriFragmentBuilder
import com.github.detouched.urlme.UriPathBuilder
import com.github.detouched.urlme.UriQueryBuilder
import java.net.URI

@Suppress("DANGEROUS_CHARACTERS")
internal data class UriBuilder(
    val pathSegments: List<String> = emptyList(),
    val queryParameters: List<Parameter> = emptyList(),
    val fragmentParameters: List<Parameter> = emptyList(),
) : UriPathBuilder, UriQueryBuilder, UriBuildTerminator {
    override fun div(pathSegment: Any): UriPathBuilder =
        div(listOf(pathSegment))

    override fun div(pathSegments: Iterable<Any>): UriPathBuilder =
        copy(
            pathSegments = buildList {
                addAll(this@UriBuilder.pathSegments)
                addAll(pathSegments.map { escape(it, this@UriBuilder::escapePathSegment) })
            }
        )

    override fun `?`(queryParameter: Any): UriQueryBuilder =
        copy(
            queryParameters = buildList {
                addAll(this@UriBuilder.queryParameters)
                add(Parameter.SingleValue(escape(queryParameter, this@UriBuilder::escapeQueryValue)))
            }
        )

    override fun `?`(queryParameter: NamedValueParameter): UriQueryBuilder =
        `?`(listOf(queryParameter))

    override fun `?`(queryParameters: Iterable<NamedValueParameter>): UriQueryBuilder =
        copy(
            queryParameters = buildList {
                addAll(this@UriBuilder.queryParameters)
                addAll(
                    queryParameters.map { (name, value) ->
                        Parameter.NamedValue(
                            escape(name, this@UriBuilder::escapeQueryValue),
                            value?.let { escape(it, this@UriBuilder::escapeQueryValue) },
                        )
                    }
                )
            }
        )

    override fun `&`(queryParameter: Any): UriQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameter: NamedValueParameter): UriQueryBuilder =
        `?`(queryParameter)

    override fun `&`(queryParameters: Iterable<NamedValueParameter>): UriQueryBuilder =
        `?`(queryParameters)

    override fun `#`(fragmentParameter: Any): UriFragmentBuilder =
        copy(
            fragmentParameters = buildList {
                addAll(this@UriBuilder.fragmentParameters)
                add(Parameter.SingleValue(escape(fragmentParameter, this@UriBuilder::escapeFragmentValue)))
            }
        ).fragmentBuildingView()

    override fun `#`(fragmentParameter: NamedValueParameter): UriFragmentBuilder =
        `#`(listOf(fragmentParameter))

    override fun `#`(fragmentParameters: Iterable<NamedValueParameter>): UriFragmentBuilder =
        copy(
            fragmentParameters = buildList {
                addAll(this@UriBuilder.fragmentParameters)
                addAll(
                    fragmentParameters.map { (name, value) ->
                        Parameter.NamedValue(
                            escape(name, this@UriBuilder::escapeFragmentValue),
                            value?.let { escape(it, this@UriBuilder::escapeFragmentValue) },
                        )
                    }
                )
            }
        ).fragmentBuildingView()

    override fun buildStringUri(): String {
        fun StringBuilder.appendParameters(
            parameters: List<Parameter>,
            sectionDelimiter: Char,
            parametersDelimiter: Char,
        ) {
            if (parameters.isNotEmpty()) {
                append(sectionDelimiter)
                parameters.forEachIndexed { index, parameter ->
                    when (parameter) {
                        is Parameter.SingleValue -> append(parameter.value)
                        is Parameter.NamedValue -> append("${parameter.name}=${parameter.value}")
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
                append(segment)
            }
            appendParameters(queryParameters, '?', '&')
            appendParameters(fragmentParameters, '#', '&')
        }
    }

    override fun buildUri(): URI = URI(buildStringUri())

    private fun escapePathSegment(pathSegment: String): String {
        // TODO path escaping
        return pathSegment
    }

    private fun escapeQueryValue(queryValue: String): String {
        // TODO query escaping
        return queryValue
    }

    private fun escapeFragmentValue(fragmentValue: String): String {
        // TODO fragment escaping
        return fragmentValue
    }

    private fun escape(value: Any, escapingFunction: (String) -> String): String =
        when (value) {
            is RawValue -> value.toString()
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
