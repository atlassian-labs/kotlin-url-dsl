package com.github.detouched.urlme

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.detouched.urlme.UriBuilder.path
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import kotlin.streams.asStream

internal class UriBuilderTest {

    internal class PathTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                path() / "" to "/",
                path() / "foo" to "/foo",
                path() / "foo" / "" to "/foo/",
                path() / "foo" / "bar" to "/foo/bar",
                path() / "this" / "" / "that" to "/this//that",
                path() / "this" / "" / "that" / "" to "/this//that/",
//                path() / "this" / "that" / "/this and that" to "/this/that/%2Fthis%20and%20that",
            )
                .map { (actual, expected) -> Arguments.of(actual.buildStringUri(), expected) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(PathTestArguments::class)
    fun `WHEN just path provided THEN uri is valid`(actual: String, expected: String) {
        assertThat(actual).isEqualTo(expected)
    }
}
