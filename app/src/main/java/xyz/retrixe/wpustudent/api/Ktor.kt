package xyz.retrixe.wpustudent.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/* FIXME
Extra headers:
  -H 'accept: application/json, text/plain, *FORWARDSLASH*' \
  -H 'accept-language: en-US,en;q=0.9' \
  -b 'visid_incap_3110609=CENSOR; incap_ses_49_3110609=CENSOR; idp_init_client_id=3; idp_session_info=CENSOR' \
  -H 'dnt: 1' \
  -H 'origin: https://mymitwpu.integratededucation.pwc.in' \
  -H 'priority: u=1, i' \
  -H 'referer: https://mymitwpu.integratededucation.pwc.in/connectportal/user/student/home/dashboard' \
  -H 'sec-ch-ua: "Not)A;Brand";v="8", "Chromium";v="138", "Google Chrome";v="138"' \
  -H 'sec-ch-ua-mobile: ?0' \
  -H 'sec-ch-ua-platform: "Linux"' \
  -H 'sec-fetch-dest: empty' \
  -H 'sec-fetch-mode: cors' \
  -H 'sec-fetch-site: same-origin' \
  -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36' \
*/

const val BASE_URL = "https://mymitwpu.integratededucation.pwc.in/"
const val CLIENT_ID = 3
const val CLIENT_SECRET = "hu5UEMnT0sg51gGtC7nC"

fun createHttpClient(): HttpClient = HttpClient(Android) {
    expectSuccess = true
    BrowserUserAgent()
    install(Logging) {
        level = LogLevel.INFO
    }
    install(ContentNegotiation) {
        json(Json { encodeDefaults = true })
    }
    defaultRequest {
        url(BASE_URL)
    }
}
