package io.github.detouched.urlme

fun interface Authority {
    fun buildString(): String
}

interface AuthorityBuilder {
    infix fun Any.`@`(hostname: Any): AuthorityPortBuilder
    infix fun Any.port(port: Int): Authority
    infix fun Any.pwd(password: Any): AuthorityHostnameBuilder
}

interface AuthorityHostnameBuilder {
    infix fun `@`(hostname: Any): AuthorityPortBuilder
}

interface AuthorityPortBuilder : Authority {
    infix fun port(port: Int): Authority
}
