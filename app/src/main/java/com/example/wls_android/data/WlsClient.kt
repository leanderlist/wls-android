package com.example.wls_android.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun getKtorClient(path: String): HttpClient {
    return HttpClient(CIO) {
        expectSuccess = false
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "wls.byleo.net"
                path(path)
            }
            headers.appendIfNameAbsent(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            )
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.HEADERS
            filter {
                it.url.host.contains("byleo.net")
            }
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 5000
            requestTimeoutMillis = 5000
        }
    }
}

fun getRequestUrl(
    line: String? = null,
    type: String? = null,
    from: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    to: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    order: String? = null,
    desc: Boolean? = null,
    active: Boolean? = null
): String {
    var urlBuilder = StringBuilder("https://wls.byleo.net/api/?")
    urlBuilder.append("from=$from")
    urlBuilder.append("&to=$to")
    if (line != null) urlBuilder.append("&line=$line")
    if (type != null) urlBuilder.append("&type=$type")
    if (order != null) urlBuilder.append("&order=$order")
    if (desc != null) urlBuilder.append("&desc=$desc")
    if (line != null) urlBuilder.append("&active=$active")
    return urlBuilder.toString()
}
