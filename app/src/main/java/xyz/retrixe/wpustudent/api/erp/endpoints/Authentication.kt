package xyz.retrixe.wpustudent.api.erp.endpoints

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.setCookie

suspend fun login(client: HttpClient, username: String, password: String): String {
    /*  curl 'https://erp.mitwpu.edu.in/login.aspx' \
          -H 'accept: *FORWARDSLASH*' \
          -H 'accept-language: en-US,en;q=0.9' \
          -H 'cache-control: no-cache' \
          -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
          -b 'ASP.NET_SessionId=CENSORED' \
          -H 'dnt: 1' \
          -H 'origin: https://erp.mitwpu.edu.in' \
          -H 'priority: u=1, i' \
          -H 'referer: https://erp.mitwpu.edu.in/login.aspx' \
          -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36' \
          -H 'x-microsoftajax: Delta=true' \
          -H 'x-requested-with: XMLHttpRequest' \
          --data-raw $'ScriptManager1=UpdatePanel1%7CbtnLogin&__LASTFOCUS=&__EVENTTARGET=&__EVENTARGUMENT=&__VIEWSTATE=CENSORED&__VIEWSTATEGENERATOR=CENSORED&hdnMsg=&hdtype=&hdloginid=&hdnFlag=R&hdnWCCSOTP=&txtUserId=CENSORED&txtPassword=CENSORED&g-recaptcha-response=CENSORED&__ASYNCPOST=true&btnLogin=Login'
    */
    val response = client.post("login.aspx") {
        contentType(ContentType.Application.FormUrlEncoded)
        header("x-microsoftajax", "Delta=true")
        header("x-requested-with", "XMLHttpRequest")
        setBody(listOf(
            "ScriptManager1" to "UpdatePanel1|btnLogin",
            "__LASTFOCUS" to "",
            "__EVENTTARGET" to "",
            "__EVENTARGUMENT" to "",
            // I don't think there's anything sensitive in this, I don't even understand it
            "__VIEWSTATE" to "/wEPDwULLTE5MzgwOTUxOTUPFgIeDkxPR0lOX0JBU0VEX09OZRYCAgMPZBYCAgEPZBYCZg9kFgoCBQ8PFgQeCEltYWdlVXJsBSZ+L05ld19Dc3MvZmF2aWNvbi9taXQtd3B1LWRyay1sb2dvLnBuZx4NQWx0ZXJuYXRlVGV4dGVkZAIGDxYCHglpbm5lcmh0bWwFBUxvZ2luZAIHD2QWAgIBDxBkDxYBZhYBEAUDQ0FTBQNDQVNnFgFmZAIKD2QWAgIBDxBkZBYBZmQCDw8WAh8DBRw8dGFibGUgY2xhc3M9J3RhYmxlJzwvdGFibGU+ZGQYwMN3dyzctC2P7iijlnDg6+I2QKDAh1J3/LDFPHw5CQ==",
            "__VIEWSTATEGENERATOR" to "C2EE9ABB",
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
    val cookies = response.setCookie()
    // We'll discard ASP.NET_SessionId for now...
    val authTokenCookie = cookies.find { it.name == "AuthToken" }?.value
        ?: throw ResponseException(response, "No AuthToken cookie found")
    println(authTokenCookie)
    return authTokenCookie
}

suspend fun logout(client: HttpClient) {
    TODO("This doesn't work yet")
}
