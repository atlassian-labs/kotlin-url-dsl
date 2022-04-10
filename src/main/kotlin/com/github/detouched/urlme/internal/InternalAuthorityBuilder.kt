package com.github.detouched.urlme.internal

import com.github.detouched.urlme.Authority
import com.github.detouched.urlme.AuthorityBuilder
import com.github.detouched.urlme.AuthorityHostnameBuilder
import com.github.detouched.urlme.AuthorityPortBuilder
import com.github.detouched.urlme.internal.escape.Escaper
import com.github.detouched.urlme.internal.escape.UrlComponentType.HOST
import com.github.detouched.urlme.internal.escape.UrlComponentType.USER_INFO_PART
import java.net.Inet6Address
import java.net.InetAddress

internal data class InternalAuthorityBuilder(
    private val username: Any? = null,
    private val password: Any? = null,
    private val host: Any? = null,
    private val port: Int? = null,
) : AuthorityBuilder, AuthorityHostnameBuilder, AuthorityPortBuilder {
    override fun Any.pwd(password: Any): AuthorityHostnameBuilder = copy(username = this, password = password)
    override fun Any.`@`(hostname: Any): AuthorityPortBuilder = copy(username = this, host = hostname)
    override fun `@`(hostname: Any): AuthorityPortBuilder = copy(host = hostname)
    override fun Any.port(port: Int): Authority = copy(host = this, port = port)
    override fun port(port: Int): Authority = copy(port = port)

    override fun buildString(): String = buildString {
        if (username == null && password != null) {
            throw IllegalStateException("Username must not be null if password provided")
        }
        username?.let { append(Escaper.escape(it, USER_INFO_PART)) }
        password?.let {
            append(':')
            append(Escaper.escape(it, USER_INFO_PART))
        }
        host?.let {
            username?.let { append('@') }
            if (host is Inet6Address) append('[')
            when (host) {
                is InetAddress -> append(host.hostAddress)
                else -> append(Escaper.escape(it, HOST))
            }
            if (host is Inet6Address) append(']')
        }
        port?.let {
            append(':')
            append(it)
        }
    }
}
