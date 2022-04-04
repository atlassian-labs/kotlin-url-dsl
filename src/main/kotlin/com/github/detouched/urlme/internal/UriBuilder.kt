package com.github.detouched.urlme.internal

import com.github.detouched.urlme.NamedValueParameter
import com.github.detouched.urlme.UriBuildTerminator
import com.github.detouched.urlme.UriFragmentBuilder
import com.github.detouched.urlme.UriPathBuilder
import com.github.detouched.urlme.UriQueryBuilder
import java.net.URI

@Suppress("DANGEROUS_CHARACTERS")
internal data class UriBuilder(
    val pathSegments: List<String>,
    val queryParameters: List<Parameter>,
    val fragmentParameters: List<Parameter>,
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
        TODO("Not yet implemented")
    }

    override fun buildUri(): URI {
        TODO("Not yet implemented")
    }

    private fun escapePathSegment(pathSegment: String): String {
        TODO("Not yet implemented")
    }

    private fun escapeQueryValue(queryValue: String): String {
        TODO("Not yet implemented")
    }

    private fun escapeFragmentValue(queryValue: String): String {
        TODO("Not yet implemented")
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
