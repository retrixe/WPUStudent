package xyz.retrixe.wpustudent.api.erp.endpoints

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.setCookie

suspend fun login(client: HttpClient, username: String, password: String): String {
    if (username == "TestAccount" && password == "TestAccount")
        return "TestAccount|TestAccount"
    /*  curl 'https://cas.mitwpu.edu.in/' \
          -H 'accept: *FORWARDSLASH*' \
          -H 'accept-language: en-US,en;q=0.9' \
          -H 'cache-control: no-cache' \
          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
          -b 'ASP.NET_SessionId=CENSORED; AuthToken=CENSORED' \ # We don't need to pass this
          -H 'origin: https://cas.mitwpu.edu.in' \
          -H 'priority: u=1, i' \
          -H 'referer: https://cas.mitwpu.edu.in/' \
          -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36' \
          -H 'x-microsoftajax: Delta=true' \
          -H 'x-requested-with: XMLHttpRequest' \
          --data-raw $'ScriptManager1=UpdatePanel1%7CbtnLogin&__LASTFOCUS=&__EVENTTARGET=btnLogin&__EVENTARGUMENT=&__VIEWSTATE=CENSORED&__VIEWSTATEGENERATOR=1869F9F9&hdnMsg=&hdtype=&hdloginid=&hdnFlag=R&hdnWCCSOTP=&txtUserId=CENSORED&txtPassword=CENSORED&g-recaptcha-response=CENSORED&__ASYNCPOST=true'
    */
    val response = client.post("") {
        contentType(ContentType.Application.FormUrlEncoded)
        header("x-microsoftajax", "Delta=true")
        header("x-requested-with", "XMLHttpRequest")
        setBody(
            listOf(
                "ScriptManager1" to "UpdatePanel1|btnLogin",
                "__LASTFOCUS" to "",
                "__EVENTTARGET" to "btnLogin",
                "__EVENTARGUMENT" to "",
                // I don't think there's anything sensitive in this, I don't even understand it
                "__VIEWSTATE" to "/wEPDwUKMTc4NTU0OTc0Nw8WAh4OTE9HSU5fQkFTRURfT05lFgICAw9kFgICAQ9kFgJmD2QWCgILDw8WBB4ISW1hZ2VVcmwFJn4vTmV3X0Nzcy9mYXZpY29uL21pdC13cHUtZHJrLWxvZ28ucG5nHg1BbHRlcm5hdGVUZXh0ZWRkAg0PFgIeCWlubmVyaHRtbAUFTG9naW5kAg8PZBYEZg9kFgICAQ8QZA8WAWYWARAFA0NBUwUDQ0FTZxYBZmQCAw9kFgICAQ8QZGQWAWZkAhEPZBYCAgMPDxYCHgRUZXh0ZWRkAhUPFgIfAwUcPHRhYmxlIGNsYXNzPSd0YWJsZSc8L3RhYmxlPmRkVh+PBqw1vecxmvT5jha0lLL93ypUeAnm5e5NRcLpnR0=\n",
                "__VIEWSTATEGENERATOR" to "1869F9F9",
                "hdnMsg" to "",
                "hdtype" to "",
                "hdloginid" to "",
                "hdnFlag" to "R",
                "hdnWCCSOTP" to "",
                "txtUserId" to username,
                "txtPassword" to password,
                // We can omit g-recaptcha-response thankfully...
                "__ASYNCPOST" to "true",
            ).formUrlEncode()
        )
    }
    // If error: <input type="hidden" name="hdnMsg" id="hdnMsg" value="USER Id/ Password Mismatch ERR-ADM05" />
    // This ERP doesn't even use HTTP status codes HAHAHAHAHAHHAHAhhahahahahahHAHAHahahhahHAA......
    val hdnMsg = response.bodyAsText().split("\n").find { it.contains("hdnMsg") }
    if (hdnMsg != null)
        throw Exception(hdnMsg.substringAfter("value=\"").substringBefore("\""))

    val cookies = response.setCookie()
    val authTokenCookie = cookies.find { it.name == "AuthToken" }?.value
        ?: throw ResponseException(response, "No AuthToken cookie found")
    val sessionIdCookie = cookies.find { it.name == "ASP.NET_SessionId" }?.value
        ?: throw ResponseException(response, "No ASP.NET_SessionId cookie found")
    return "$authTokenCookie|$sessionIdCookie"
}

@Suppress("UNUSED_PARAMETER", "RedundantSuspendModifier")
suspend fun logout(client: HttpClient) {
    // TODO: No-op, it's a pain to implement (storing __VIEWSTATE in login), and tokens expire quickly anyway
}
