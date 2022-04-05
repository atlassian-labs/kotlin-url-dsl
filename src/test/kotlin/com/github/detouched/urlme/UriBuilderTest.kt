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
                path() / "this" / "that" / "/this and that" to "/this/that/%2Fthis%20and%20that",
                path() / "Zürich" to "/Z%C3%BCrich",
                path() / "😀" to "/%F0%9F%98%80",
                path() / "привет" to "/%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82",
                path() / "foo/bar" to "/foo%2Fbar",
                path() / "foo/bar".raw() to "/foo/bar",
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
