package xyz.retrixe.wpustudent.api.endpoints

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.retrixe.wpustudent.api.CLIENT_ID
import xyz.retrixe.wpustudent.api.CLIENT_SECRET

@Serializable
private data class OAuth2CodeRequest(
    @SerialName("UserId") val userId: String,
    @SerialName("Password") val password: String,
    @SerialName("Captcha") val captcha: String = "-",
    @SerialName("LoginUsing") val loginUsing: String = "OTP",
    @SerialName("AppRef") val appRef: String = "studentportal",
)

@Serializable
private data class OAuth2CodeResponse(@SerialName("Item") val item: OAuth2CodeItem)

@Serializable
private data class OAuth2CodeItem(
    val emailId: String,
    val redirectUrl: String,
)

suspend fun getOAuthCode(client: HttpClient, username: String, password: String): String {
    /* Note: This hash in the query params seems optional; I'm not sure how it's calculated
        curl 'https://mymitwpu.integratededucation.pwc.in/sso/api/account/oauth2/token?hash=zOMgcPkkFAmrQQhxscW8' \
          -H 'content-type: application/json' \
          -H 'x-applicationname: oneportal' \
          -H 'x-appsecret;' \
          -H 'x-requestfrom: web' \
          --data-raw $'{"UserId":"ibrahim.ansari@mitwpu.edu.in","Password":"CENSORED","Captcha":"-","LoginUsing":"OTP","AppRef":"studentportal"}'
    */
    val response = client.post("sso/api/account/oauth2/token") {
        contentType(ContentType.Application.Json)
        header("x-applicationname", "oneportal")
        header("x-appsecret", "")
        header("x-requestfrom", "web")
        setBody(OAuth2CodeRequest(username, password))
    }
    val body: OAuth2CodeResponse = response.body()
    val url = Url(body.item.redirectUrl)
    return url.parameters["code"]!!
}

@Serializable
private data class AccessTokenRequest(
    @SerialName("ClientId") val clientId: Int = CLIENT_ID,
    @SerialName("ClientSecret") val clientSecret: String = CLIENT_SECRET,
    @SerialName("Code") val code: String,
)

/* {
  "StatusCode": 200,
  "Item": {
    "Identity": {
      "clientId": 3,
      "AccessToken": "CENSORED",
      "RefreshToken": "CENSORED",
      "Scope": "openid",
      "TokenType": "Bearer",
      "ExpiresIn": 29079.47503
    },
    "UserInfo": {
      "UserId": 78423,
      "EmailId": "ibrahim.ansari@mitwpu.edu.in",
      "FirstName": "Ibrahim",
      "LastName": "Ansari",
      "UserTypeInfo": {
        "Code": "ST",
        "DisplayName": "Student"
      },
      "MobileNumber": "CENSORED",
      "DateOfBirth": "2004-12-02T00:00:00",
      "ProfilePicture": "",
      "RoleUserMaps": [
        {
          "RoleId": 109,
          "RoleCode": "ST",
          "RoleName": "Student",
          "IsPrefered": false
        }
      ],
      "IsFirstTimeLogin": false,
      "IsMFAEnabledWithOTP": false,
      "IsLock": false,
      "LockCounter": 0
    }
  }
} */
@Serializable
private data class AccessTokenResponse(@SerialName("Item") val item: AccessTokenItem)

@Serializable
private data class AccessTokenItem(@SerialName("Identity") val identity: AccessTokenIdentity)

@Serializable
private data class AccessTokenIdentity(
    @SerialName("AccessToken") val accessToken: String,
    @SerialName("RefreshToken") val refreshToken: String,
)

suspend fun getAccessToken(client: HttpClient, code: String): String {
    /*  curl 'https://mymitwpu.integratededucation.pwc.in/sso/oauth2/access_token' \
          -H 'content-type: application/json' \
          -H 'x-applicationname: connectportal' \
          -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
          -H 'x-requestfrom: web' \
          --data-raw '{"ClientId":3,"ClientSecret":"hu5UEMnT0sg51gGtC7nC","Code":"RETURNED FROM REQ 1"}'
    */
    val response = client.post("sso/oauth2/access_token") {
        contentType(ContentType.Application.Json)
        header("x-applicationname", "connectportal")
        header("x-appsecret", CLIENT_SECRET)
        header("x-requestfrom", "web")
        setBody(AccessTokenRequest(code = code))
    }
    val body: AccessTokenResponse = response.body()
    return body.item.identity.accessToken
}

// FIXME: https://mymitwpu.integratededucation.pwc.in/sso/user/oauth2/refresh-token

suspend fun logout(client: HttpClient) {
    // TODO: How in the blazes does this API work?
    // curl 'https://mymitwpu.integratededucation.pwc.in/sso/user/oauth2/logout?autoId=UNKNOWN&client_id=3&clientSecret=hu5UEMnT0sg51gGtC7nC'
    val characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val autoId = (0..19).map { _ -> characters.random() }.joinToString("")
    client.get("sso/user/oauth2/logout") {
        url.parameters.append("autoId", autoId)
        url.parameters.append("client_id", CLIENT_ID.toString())
        url.parameters.append("clientSecret", CLIENT_SECRET)
    }
}
