package com.github.detouched.urlme

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.github.detouched.urlme.UriBuilder.path
import org.junit.jupiter.api.assertThrows
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
                path() / listOf("foo", "bar") to "/foo/bar",
            )
                .map { (actual, expected) -> Arguments.of(actual.buildStringUri(), expected) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(PathTestArguments::class)
    fun `WHEN just path provided THEN uri is valid`(actual: String, expected: String) {
        assertThat(actual).isEqualTo(expected)
    }

    internal class QueryTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                path() / "" `?` ("foo" to "bar") to "/?foo=bar",
                path() / "foo" `?` "single" to "/foo?single",
                path() / "foo" `?` ("name" to null) to "/foo?name=",
                path() / "foo" `?` ("name" to "value") to "/foo?name=value",
                path() / "foo" `?` "single" `&` ("bar" to null) `&` ("name" to "value") to "/foo?single&bar=&name=value",
                path() / "foo" `?` listOf("foo" to "bar", "baz" to null) to "/foo?foo=bar&baz=",
                path() / "foo" `?` "this / that" to "/foo?this%20/%20that",
                path() / "foo" `?` "Zürich" to "/foo?Z%C3%BCrich",
                path() / "foo" `?` ("🤩" to "мир") to "/foo?%F0%9F%A4%A9=%D0%BC%D0%B8%D1%80",
                path() / "foo" `?` "foo=bar" to "/foo?foo%3Dbar",
                path() / "foo" `?` "foo=bar".raw() to "/foo?foo=bar",
                path() / "foo" `?` ("foo=bar" to "baz&qux") to "/foo?foo%3Dbar=baz%26qux",
                path() / "foo" `?` ("foo=bar".raw() to "baz&qux".raw()) to "/foo?foo=bar=baz&qux",
            )
                .map { (actual, expected) -> Arguments.of(actual.buildStringUri(), expected) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(QueryTestArguments::class)
    fun `WHEN query parameters provided THEN uri is valid`(actual: String, expected: String) {
        assertThat(actual).isEqualTo(expected)
    }

    internal class FragmentTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                path() / "" `#` ("foo" to "bar") to "/#foo=bar",
                path() / "foo" `#` "single" to "/foo#single",
                path() / "foo" `#` ("name" to null) to "/foo#name=",
                path() / "foo" `#` ("name" to "value") to "/foo#name=value",
                path() / "foo" `#` "single" `&` ("bar" to null) `&` ("name" to "value") to "/foo#single&bar=&name=value",
                path() / "foo" `#` listOf("foo" to "bar", "baz" to null) to "/foo#foo=bar&baz=",
                path() / "foo" `#` "this / that" to "/foo#this%20/%20that",
                path() / "foo" `#` "Zürich" to "/foo#Z%C3%BCrich",
                path() / "foo" `#` ("🤩" to "мир") to "/foo#%F0%9F%A4%A9=%D0%BC%D0%B8%D1%80",
                path() / "foo" `#` "foo=bar" to "/foo#foo=bar",
                path() / "foo" `#` "foo&bar" to "/foo#foo&bar",
                path() / "foo" `#` "foo=bar" `&` "foo&bar" to "/foo#foo%3Dbar&foo%26bar",
                path() / "foo" `#` "foo[bar]" to "/foo#foo%5Bbar%5D",
                path() / "foo" `#` "foo[bar]".raw() to "/foo#foo[bar]",
                path() / "foo" `#` ("foo=bar" to "baz&qux") to "/foo#foo%3Dbar=baz%26qux",
                path() / "foo" `#` ("foo=bar".raw() to "baz&qux".raw()) to "/foo#foo=bar=baz&qux",
            )
                .map { (actual, expected) -> Arguments.of(actual.buildStringUri(), expected) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(FragmentTestArguments::class)
    fun `WHEN fragment parameters provided THEN uri is valid`(actual: String, expected: String) {
        assertThat(actual).isEqualTo(expected)
    }

    internal class QueryAndFragmentTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                path() / "" `?` "foo" `#` ("bar" to "baz") to "/?foo#bar=baz",
                path() / "foo" `?` ("foo" to "bar") `#` "baz" to "/foo?foo=bar#baz",
                path() / "foo" `?` ("bar" to null) `#` ("name" to null) to "/foo?bar=#name=",
                path() / "foo" / "bar" `?` ("hello" to "world") `#` ("name" to "value") to "/foo/bar?hello=world#name=value",
                path() / "😁" / "Zürich" `?` "🙃" `#` "😉" to "/%F0%9F%98%81/Z%C3%BCrich?%F0%9F%99%83#%F0%9F%98%89",
                path() / "foo/bar".raw() `?` "baz=qux".raw() `#` "fl@t".raw() to "/foo/bar?baz=qux#fl@t",
            )
                .map { (actual, expected) -> Arguments.of(actual.buildStringUri(), expected) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(QueryAndFragmentTestArguments::class)
    fun `WHEN query and fragments parameters provided THEN uri is valid`(actual: String, expected: String) {
        assertThat(actual).isEqualTo(expected)
    }

    internal class InvalidUriTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                { path() / "foo" `?` "" },
                { path() / "foo" `?` "".raw() },
                { path() / "foo" `?` "" `&` ("bar" to "baz") },
                { path() / "foo" `?` ("" to null) `&` ("bar" to "baz") },
                { path() / "foo" `?` ("" to "bar") `&` ("bar" to "baz") },
                { path() / "foo" `#` "" },
                { path() / "foo" `#` "".raw() },
                { path() / "foo" `#` "" `&` ("bar" to "baz") },
                { path() / "foo" `#` ("" to null) `&` ("bar" to "baz") },
                { path() / "foo" `#` ("" to "bar") `&` ("bar" to "baz") },
            )
                .map { Arguments.of(it) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidUriTestArguments::class)
    fun `WHEN invalid uri built THEN exception is thrown`(buildUri: () -> Nothing) {
        assertThrows<IllegalArgumentException> { buildUri() }
    }
}
