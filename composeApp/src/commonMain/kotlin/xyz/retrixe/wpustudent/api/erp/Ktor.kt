package xyz.retrixe.wpustudent.api.erp

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.cookie
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import xyz.retrixe.wpustudent.api.erp.mocks.MOCKS
import xyz.retrixe.wpustudent.kmp.PlatformHttpClientEngineFactory

const val BASE_URL = "https://cas.mitwpu.edu.in/"

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

fun createHttpClient(token: String?): HttpClient = HttpClient(
    if (token == "TestAccount|TestAccount") MockEngineFactory else PlatformHttpClientEngineFactory
) {
    expectSuccess = true
    BrowserUserAgent()
    install(Logging) {
        level = LogLevel.INFO
    }
    install(ContentNegotiation) {
        json(json)
    }
    defaultRequest {
        url(BASE_URL)
        if (token != null) {
            cookie("AuthToken", token.split("|")[0])
            cookie("ASP.NET_SessionId", token.split("|")[1])
        }
    }
}

const val USER_IMG = "data:image/svg+xml;base64,c2tpcCB0aGlz"

data object MockEngineFactory : HttpClientEngineFactory<MockEngineConfig> {
    override fun create(block: MockEngineConfig.() -> Unit): HttpClientEngine =
        MockEngine.create {
            block(this)
            requestHandlers.add { request ->
                val mock = MOCKS[request.url.encodedPath]
                if (mock == null) {
                    respond("Unknown API called", HttpStatusCode.NotFound)
                } else {
                    mock(request)
                }
            }
        }
}
