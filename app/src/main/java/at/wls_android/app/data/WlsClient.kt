package at.wls_android.app.data

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

fun getKtorClient(path: String, baseUrl: String): HttpClient {
    val protocol = if (baseUrl.startsWith("https")) URLProtocol.HTTPS else URLProtocol.HTTP
    val host = baseUrl.removePrefix("https://").removePrefix("http://")

    return HttpClient(CIO) {
        expectSuccess = false
        defaultRequest {
            url {
                this.protocol = protocol
                this.host = host
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
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 5000
            requestTimeoutMillis = 5000
        }
    }
}