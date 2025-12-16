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
          --data-raw $'ScriptManager1=UpdatePanel1%7CbtnLogin&__LASTFOCUS=&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=CENSORED&__VIEWSTATEGENERATOR=1869F9F9&hdnMsg=&hdtype=&hdloginid=&hdnFlag=R&hdnWCCSOTP=&txtUserId=CENSORED&txtPassword=CENSORED&g-recaptcha-response=CENSORED&__ASYNCPOST=true&btnLogin=Login'
    */
    val response = client.post("") {
        contentType(ContentType.Application.FormUrlEncoded)
        header("x-microsoftajax", "Delta=true")
        header("x-requested-with", "XMLHttpRequest")
        setBody(listOf(
            "ScriptManager1" to "UpdatePanel1|btnLogin",
            "__LASTFOCUS" to "",
            "__EVENTTARGET" to "",
            "__EVENTARGUMENT" to "",
            // I don't think there's anything sensitive in this, I don't even understand it
            "__VIEWSTATE" to "/wEPDwULLTE5MzgwOTUxOTUPFgIeDkxPR0lOX0JBU0VEX09OZRYCAgMPZBYCAgEPZBYCZg9kFgoCBQ8PFgQeCEltYWdlVXJsBSZ+L05ld19Dc3MvZmF2aWNvbi9taXQtd3B1LWRyay1sb2dvLnBuZx4NQWx0ZXJuYXRlVGV4dGVkZAIGDxYCHglpbm5lcmh0bWwFBUxvZ2luZAIHD2QWAgIBDxBkDxYBZhYBEAUDQ0FTBQNDQVNnFgFmZAIKD2QWAgIBDxBkZBYBZmQCDw8WAh8DBRw8dGFibGUgY2xhc3M9J3RhYmxlJzwvdGFibGU+ZGQX+mtROtdU8DMiVPnteawM/rwmjMFn5+6XesnlGhpgFg==",
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
            "btnLogin" to "Login",
        ).formUrlEncode())
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
