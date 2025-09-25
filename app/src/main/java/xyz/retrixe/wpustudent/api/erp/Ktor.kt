package xyz.retrixe.wpustudent.api.erp

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
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

const val BASE_URL = "https://erp.mitwpu.edu.in/"

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

fun createHttpClient(token: String?): HttpClient = HttpClient(
    if (token == "TestAccount|TestAccount") MockEngineFactory else Android
) {
    println(token)
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

const val USER_IMG = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAACgUlEQVR4AeyWO2gVQRSGg9jZqKgogtgJFj4aEXyAjQqKiIW1L7RRsPFRCAoWolgqCuKrFlEUBW0UxMLCLqRIlSIkBEJIkzbk+8LMQjY7O7N5Qkg4X85k5v//PWzuvXvX9Czzz+oAc7kDZ/mv3YOPMBhw7Z5nbJVX1wFeEv0JHsI52B5w7Z5natguq9IBthH3A67BKNyCE7A14No9z9So1cNxe5UMsI6Ib3Ac/sAueAo/YSTg2j3P1KjVoxdJukoGuIt9P7yDozAGqfJMjVo9elPa6f3cAAdRGTJAvw6lpVaPXjOSvtwABqzF/QImoLTU6tFrRtKXG2BHcPaF3qVFT8xo9OYGWB9c/0Pv0qInZjR6cwNsCK7J0Lu06IkZjd7cAHH6vY3u9s3oiRmN6twAT4JrT+hdWvTEjEZvboD3wXWHvhFKS60e9THD9SxyA/hK/oBrE3yG0lKrR68ZSV9uAI0+5XxFH+GPt9D28eqZGrV69GJJV8kA/dhPwm+4AL1wG06DDxxx7Z5natTq0YssXSUD6PYpd4zFF9gJj+ErDAVcu+eZGrV6OG6vkgF8G/lx+o+oM5ArNWr16G3V5wbwG84vEh7BARiG53AZDsPmgGv3PFOjVo9eM5A1V9sAD7D4DWcf/Ttcgd3gk+4N/S94m8W1e56pUatHrxlmIZ9dqQE03A/yV/RT8BrGIVdq1OrRq94sM13PoGmAmyg00Hou8esqzLX0mqHfTLNdV9QH8JYpVHCRX76nafMqM8wyxGyv4Xqa+gAKfOU+49SvVbQFKbPMNNtrVKH1AbZw4sfnDfpCl5lme40quz7AIU7Ow2KV2V6jyq8PUB0s1WLlD5C7k1MAAAD//8L+yFcAAAAGSURBVAMA5mFyQRCxvu8AAAAASUVORK5CYII="

data object MockEngineFactory : HttpClientEngineFactory<MockEngineConfig> {
    override fun create(block: MockEngineConfig.() -> Unit): HttpClientEngine =
        MockEngine.create {
            block(this)
            requestHandlers.add { request ->
                if (request.url.encodedPath == "/ERP_Main.aspx") {
                    respond("""
                        <html>
                        <body>
                        <span id="span_userid">1032233137</span>
                        <h6 id="span_username">John Doe</h6>
                        <span id="span_regular">SEMESTER-V(First)</span>
                        <span id="span_courseyear">TY BTech  -CSE-A</span>
                        <img id="imgprofile" src="$USER_IMG" />
                        </body>
                        </html>
                    """.trimIndent(), HttpStatusCode.OK)
                } else {
                    respond("Unknown API called", HttpStatusCode.NotFound)
                }
            }
        }
}
