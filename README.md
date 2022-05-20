# Kotlin URL DSL

[![Atlassian license](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat-square)](LICENSE)
[![Maven Central status](https://img.shields.io/maven-central/v/com.atlassian.kotlin.dsl/url.svg?style=flat-square)](https://search.maven.org/search?q=g:com.atlassian.kotlin.dsl%20a:url)
![CI](https://github.com/atlassian-labs/kotlin-url-dsl/workflows/CI/badge.svg?branch=main)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](CONTRIBUTING.md)

This library allows to represent a URL as Kotlin code which visually looks very similar
to the URL it generates.

## Installation

Library artifacts are published to [Maven Central](https://search.maven.org/search?q=g:com.atlassian.kotlin.dsl%20a:url).

### Maven

```xml
<dependency>
    <groupId>com.atlassian.kotlin.dsl</groupId>
    <artifactId>url</artifactId>
    <version>0.1</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("com.atlassian.kotlin.dsl", "url", "0.1")
}
```

## Usage

The DSL is type safe. At any complete state of a URL the builder can be converted
to [`URI`](https://docs.oracle.com/javase/8/docs/api/java/net/URI.html) or
[`String`](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html) instance
representing that URL:

```kotlin
val builder: UrlBuildTerminator = ...

val uri: URI = builder.buildUri()
val stringUri: String = builder.buildStringUri()
```

### Relative URLs

Relative URL starts with path elements and may contain query parameters and fragment:

```kotlin
UrlBuilder / ""                                                              //  /
UrlBuilder / "path" / "elements"                                             //  /path/elements
UrlBuilder / "path" `?` "query"                                              //  /path?query
UrlBuilder / "path" `?` ("param" to "value")                                 //  /path?param=value
UrlBuilder / "path" `?` ("param1" to "value1") `&` ("param2" to "value2")    //  /path?param1=value1&param2=value2
UrlBuilder / "path" `?` ("param1" to "value1") `&` "param2"                  //  /path?param1=value1&param2
UrlBuilder / "path" `?` ("param1" to null) `&` "param2"                      //  /path?param1=&param2
UrlBuilder / "path" `#` "fragment"                                           //  /path#fragment
UrlBuilder / "path" `#` "fragment1" `&` "fragment2"                          //  /path#fragment1&fragment2
UrlBuilder / "path" `?` ("param" to "value") `#` "fragment"                  //  /path?param=value#fragment
```

Note that in these examples and below the arguments (path, query and fragment items) can be of any type.
Unless they are of `String` type, their `toString()` representation will be used by the builder.
See also notes on [escaping](#escaping).

### Absolute URLs

Absolute URLs include the schema, optional authority, path and optional query and fragment. The builder syntax
is a bit different in this case:

```kotlin
UrlBuilder { "https" % "example.com" }                                      //  https://example.com
UrlBuilder { "https" % "example.com" / "path" }                             //  https://example.com/path
UrlBuilder { "https" % "example.com" / "path" `?` "query" }                 //  https://example.com/path?query
// Path, query and fragment support is same to the relative URLs
```

Authority in turn, can be either a plain hostname or a builder (represented as lambda) which additionally
supports combinations of _port_, _username_ and _password_:

```kotlin
UrlBuilder { "https" % "example.com" / "path" }                                            //  https://example.com/path
UrlBuilder { "https" % { "example.com" port 8080 } / "path" }                              //  https://example.com:8080/path
UrlBuilder { "https" % { "user" `@` "example.com" } / "path" }                             //  https://user@example.com/path
UrlBuilder { "https" % { "user" pwd "password" `@` "example.com" } / "path" }              //  https://user:password@example.com/path
UrlBuilder { "https" % { "user" pwd "password" `@` "example.com" port 8080 } / "path" }    //  https://user:password@example.com:8080/path
```

### Using base URLs

The builder allows to specify a base URL. In this case the builder will be functionally equivalent
to a [relative URL builder](#relative-urls) with all elements of the base URL (scheme, authority,
path, query and fragment) preserved as is. That is, the scheme and authority will be copied from
the base URL, the path will be appended to the path of the base URL, query and fragment parameters
will be added to those specified in the base URL.

```kotlin
UrlBuilder("https://example.com")                                                      //  https://example.com
UrlBuilder("https://example.com") / "path"                                             //  https://example.com/path
UrlBuilder("https://example.com/") / "path"                                            //  https://example.com//path
UrlBuilder("https://example.com?param1=value1") / "path" `?` ("param2" to "value2")    //  https://example.com/path?param1=value1&param2=value2
UrlBuilder("https://example.com#fragment1") `?` "query" `#` "fragment2"                //  https://example.com?query#fragment1&fragment2
```

### Escaping

All arguments passed in to the builder will be escaped by default (except for the [base URL](#using-base-urls)).
The mechanism follows [RFC 3986](https://datatracker.ietf.org/doc/html/rfc3986) for every component type of the URL.

To disable argument escaping and use its representation as is (e.g. when such value has been percent-encoded
externally), pass it with `.raw()` marker. This is an extension function available for `Any` type:

```kotlin
UrlBuilder / "foo/bar"          //  /foo%2Fbar
UrlBuilder / "foo/bar".raw()    //  /foo/bar
```

#### Fragment escaping

Escaping mechanism for the URL fragment slightly deviates from the RFC. Unlike the RFC definition, this library treats
fragment similar to the query in the sense that it may consist of multiple key-value parameters. Escaping mechanism
is adapted to this in the following way: if the fragment is a single value (not a key-value), it is encoded like
the RFC suggests, whereas if it is a key-value or contains multiple items, it is encoded like query component.
That is, in the latter case `=` and `&` characters are also percent-encoded should they occur in the value of such
fragment items.

```kotlin
UrlBuilder / "" `#` "foo&=bar"               //  /#foo&=bar
UrlBuilder / "" `#` ("foo&=bar" to "baz")    //  /#foo%26%3Dbar=baz
UrlBuilder / "" `#` "foo&=bar" `&` "baz"     //  /#foo%26%3Dbar&baz
```
