package com.github.detouched.urlme

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import kotlin.streams.asStream

internal class UrlBuilderTest {

    internal class PathTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                UrlBuilder / "" to "/",
                UrlBuilder / "foo" to "/foo",
                UrlBuilder / "foo" / "" to "/foo/",
                UrlBuilder / "foo" / "bar" to "/foo/bar",
                UrlBuilder / "this" / "" / "that" to "/this//that",
                UrlBuilder / "this" / "" / "that" / "" to "/this//that/",
                UrlBuilder / "this" / "that" / "/this and that" to "/this/that/%2Fthis%20and%20that",
                UrlBuilder / "Zürich" to "/Z%C3%BCrich",
                UrlBuilder / "😀" to "/%F0%9F%98%80",
                UrlBuilder / "привет" to "/%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82",
                UrlBuilder / "foo/bar" to "/foo%2Fbar",
                UrlBuilder / "foo/bar".raw() to "/foo/bar",
                UrlBuilder / listOf("foo", "bar") to "/foo/bar",
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
                UrlBuilder / "" `?` ("foo" to "bar") to "/?foo=bar",
                UrlBuilder / "foo" `?` "single" to "/foo?single",
                UrlBuilder / "foo" `?` ("name" to null) to "/foo?name=",
                UrlBuilder / "foo" `?` ("name" to "value") to "/foo?name=value",
                UrlBuilder / "foo" `?` "single" `&` ("bar" to null) `&` ("name" to "value") to "/foo?single&bar=&name=value",
                UrlBuilder / "foo" `?` listOf("foo" to "bar", "baz" to null) to "/foo?foo=bar&baz=",
                UrlBuilder / "foo" `?` "this / that" to "/foo?this%20/%20that",
                UrlBuilder / "foo" `?` "Zürich" to "/foo?Z%C3%BCrich",
                UrlBuilder / "foo" `?` ("🤩" to "мир") to "/foo?%F0%9F%A4%A9=%D0%BC%D0%B8%D1%80",
                UrlBuilder / "foo" `?` "foo=bar" to "/foo?foo%3Dbar",
                UrlBuilder / "foo" `?` "foo=bar".raw() to "/foo?foo=bar",
                UrlBuilder / "foo" `?` ("foo=bar" to "baz&qux") to "/foo?foo%3Dbar=baz%26qux",
                UrlBuilder / "foo" `?` ("foo=bar".raw() to "baz&qux".raw()) to "/foo?foo=bar=baz&qux",
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
                UrlBuilder / "" `#` ("foo" to "bar") to "/#foo=bar",
                UrlBuilder / "foo" `#` "single" to "/foo#single",
                UrlBuilder / "foo" `#` ("name" to null) to "/foo#name=",
                UrlBuilder / "foo" `#` ("name" to "value") to "/foo#name=value",
                UrlBuilder / "foo" `#` "single" `&` ("bar" to null) `&` ("name" to "value") to "/foo#single&bar=&name=value",
                UrlBuilder / "foo" `#` listOf("foo" to "bar", "baz" to null) to "/foo#foo=bar&baz=",
                UrlBuilder / "foo" `#` "this / that" to "/foo#this%20/%20that",
                UrlBuilder / "foo" `#` "Zürich" to "/foo#Z%C3%BCrich",
                UrlBuilder / "foo" `#` ("🤩" to "мир") to "/foo#%F0%9F%A4%A9=%D0%BC%D0%B8%D1%80",
                UrlBuilder / "foo" `#` "foo=bar" to "/foo#foo=bar",
                UrlBuilder / "foo" `#` "foo&bar" to "/foo#foo&bar",
                UrlBuilder / "foo" `#` "foo=bar" `&` "foo&bar" to "/foo#foo%3Dbar&foo%26bar",
                UrlBuilder / "foo" `#` "foo[bar]" to "/foo#foo%5Bbar%5D",
                UrlBuilder / "foo" `#` "foo[bar]".raw() to "/foo#foo[bar]",
                UrlBuilder / "foo" `#` ("foo=bar" to "baz&qux") to "/foo#foo%3Dbar=baz%26qux",
                UrlBuilder / "foo" `#` ("foo=bar".raw() to "baz&qux".raw()) to "/foo#foo=bar=baz&qux",
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
                UrlBuilder / "" `?` "foo" `#` ("bar" to "baz") to "/?foo#bar=baz",
                UrlBuilder / "foo" `?` ("foo" to "bar") `#` "baz" to "/foo?foo=bar#baz",
                UrlBuilder / "foo" `?` ("bar" to null) `#` ("name" to null) to "/foo?bar=#name=",
                UrlBuilder / "foo" / "bar" `?` ("hello" to "world") `#` ("name" to "value") to "/foo/bar?hello=world#name=value",
                UrlBuilder / "😁" / "Zürich" `?` "🙃" `#` "😉" to "/%F0%9F%98%81/Z%C3%BCrich?%F0%9F%99%83#%F0%9F%98%89",
                UrlBuilder / "foo/bar".raw() `?` "baz=qux".raw() `#` "fl@t".raw() to "/foo/bar?baz=qux#fl@t",
            )
                .map { (actual, expected) -> Arguments.of(actual.buildStringUri(), expected) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(QueryAndFragmentTestArguments::class)
    fun `WHEN query and fragments parameters provided THEN uri is valid`(actual: String, expected: String) {
        assertThat(actual).isEqualTo(expected)
    }

    internal class AuthorityTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                UrlBuilder { "file" % "" / "foo" / "bar" } to "file:///foo/bar",
                UrlBuilder { "http" % "example.com" } to "http://example.com",
                UrlBuilder { "https" % "example.com" / "" } to "https://example.com/",
                UrlBuilder { "https" % { "example.com" port 8080 } / "foo" } to "https://example.com:8080/foo",
                UrlBuilder { "https" % { "192.0.2.16" port 443 } } to "https://192.0.2.16:443",
                UrlBuilder { "https" % { "admin" `@` "example.com" } } to "https://admin@example.com",
                UrlBuilder { "https" % { "admin" `@` "example.com" } / "foo" } to "https://admin@example.com/foo",
                UrlBuilder { "http" % { "admin" `@` "example.com" port 8080 } / "foo" } to "http://admin@example.com:8080/foo",
                UrlBuilder { "http" % { "admin" pwd "secret" `@` "example.com" port 8080 } / "foo" } to "http://admin:secret@example.com:8080/foo",
                UrlBuilder { "http" % { "foo bar" pwd "baz:qux" `@` "example.com" port 8080 } / "foo" } to "http://foo%20bar:baz%3Aqux@example.com:8080/foo",
                UrlBuilder { "http" % "2001:0db8:0000:0000:0000:8a2e:0370:7334".ip() } to "http://[2001:db8:0:0:0:8a2e:370:7334]",
                UrlBuilder { "http" % "[2001:0db8:0000:0000:0000:8a2e:0370:7334]".raw() } to "http://[2001:0db8:0000:0000:0000:8a2e:0370:7334]",
                UrlBuilder { "http" % { "2001:0db8:0000:0000:0000:8a2e:0370:7334".ip() port 7777 } } to "http://[2001:db8:0:0:0:8a2e:370:7334]:7777",
                UrlBuilder { "http" % { "[2001:0db8:0000:0000:0000:8a2e:0370:7334]".raw() port 7788 } } to "http://[2001:0db8:0000:0000:0000:8a2e:0370:7334]:7788",
                UrlBuilder { "http" % "2001:db8::8a2e:370:7334".ip() } to "http://[2001:db8:0:0:0:8a2e:370:7334]",
                UrlBuilder { "http" % "[2001:db8::8a2e:370:7334]".raw() } to "http://[2001:db8::8a2e:370:7334]",
                UrlBuilder { "http" % "127.0.0.1".ip() } to "http://127.0.0.1",
                UrlBuilder { "http" % { "192.45.23.77".ip() port 123 } } to "http://192.45.23.77:123",
            )
                .map { (actual, expected) -> Arguments.of(actual.buildStringUri(), expected) }
                .asStream()
    }

    @ParameterizedTest
    @ArgumentsSource(AuthorityTestArguments::class)
    fun `WHEN authority provided THEN uri is valid`(actual: String, expected: String) {
        assertThat(actual).isEqualTo(expected)
    }

    internal class InvalidUrlTestArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext) =
            sequenceOf(
                { UrlBuilder { "1ttp" % "ex.com" } } to "Non-letter first character of scheme",
                { UrlBuilder { "ht!tp" % "ex.com" } } to "Invalid character in scheme",
                { UrlBuilder / "foo" `?` "" } to "Empty single query parameter",
                { UrlBuilder / "foo" `?` "".raw() } to "Empty raw single query parameter",
                { UrlBuilder / "foo" `?` "" `&` ("bar" to "baz") } to "One of query parameters empty",
                { UrlBuilder / "foo" `?` ("" to null) `&` ("bar" to "baz") } to "One of query parameters with empty name 1",
                { UrlBuilder / "foo" `?` ("" to "bar") `&` ("bar" to "baz") } to "One of query parameters with empty name 2",
                { UrlBuilder / "foo" `#` "" } to "Empty single fragment parameter",
                { UrlBuilder / "foo" `#` "".raw() } to "Empty raw single fragment parameter",
                { UrlBuilder / "foo" `#` "" `&` ("bar" to "baz") } to "One of fragment parameters empty",
                { UrlBuilder / "foo" `#` ("" to null) `&` ("bar" to "baz") } to "One of fragment parameters with empty name 1",
                { UrlBuilder / "foo" `#` ("" to "bar") `&` ("bar" to "baz") } to "One of fragment parameters with empty name 2",
            )
                .map { (build, description) -> Arguments.of(description, build) }
                .asStream()
    }

    @ParameterizedTest(name = "{0}")
    @ArgumentsSource(InvalidUrlTestArguments::class)
    fun `WHEN invalid uri built THEN exception is thrown`(description: String, buildUri: () -> Nothing) {
        assertThrows<IllegalArgumentException> { buildUri() }
    }
}
