package xyz.retrixe.wpustudent.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.retrixe.wpustudent.R
import xyz.retrixe.wpustudent.ui.components.PasswordTextField
import xyz.retrixe.wpustudent.ui.components.PlainTooltipBox
import xyz.retrixe.wpustudent.utils.handleKeyEvent

/*
Hash seems optional

https://ktor.io/docs/client-bearer-auth.html
https://ktor.io/docs/client-serialization.html

curl 'https://mymitwpu.integratededucation.pwc.in/sso/api/account/oauth2/token?hash=zOMgcPkkFAmrQQhxscW8' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: oneportal' \
  -H 'x-appsecret;' \
  -H 'x-requestfrom: web' \
  --data-raw $'{"UserId":"ibrahim.ansari@mitwpu.edu.in","Password":"CENSORED","Captcha":"-","LoginUsing":"OTP","AppRef":"studentportal"}'

curl 'https://mymitwpu.integratededucation.pwc.in/sso/oauth2/access_token' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"ClientId":3,"ClientSecret":"hu5UEMnT0sg51gGtC7nC","Code":"RETURNED FROM REQ 1"}'

curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/connect-portal/api/studentloginbasicinfo' \
  -H 'authorization: Bearer BEARER' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web'

curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/attendance/api/attendance/summary' \
  -H 'authorization: Bearer BEARER TOKEN' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"StartDate":null,"EndDate":"2025-05-07","ModeName":"term","StudentUniqueId":"c9bef136-396a-441e-9370-876fda382b20","SelectedModuleId":null,"SelectedTermId":null}'

curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/student-attendance/attendancedropdown' \
  -H 'authorization: Bearer BEARER' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"StudentUniqueID":"c9bef136-396a-441e-9370-876fda382b20"}'

curl 'https://mymitwpu.integratededucation.pwc.in/apigateway/student-attendance/studentattendancesummary' \
  -H 'authorization: Bearer BEARER TOKEN' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: connectportal' \
  -H 'x-appsecret: hu5UEMnT0sg51gGtC7nC' \
  -H 'x-requestfrom: web' \
  --data-raw '{"StudentUniqueID":"c9bef136-396a-441e-9370-876fda382b20","CourseFamilyId":35,"TermCodeId":4,"CourseList":[{"ID":1773,"Name":"Probability and Statistics"}],"StartDate":"2025-05-01","EndDate":"2025-05-07","TermStartDate":"2025-01-01","TermEndDate":"2025-07-06"}'

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

@Composable
fun LoginScreen(paddingValues: PaddingValues) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val (passwordFocus, loginButtonFocus) = remember { FocusRequester.createRefs() }

    fun login() {
        // FIXME
    }

    Column(
        Modifier.padding(paddingValues).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(48.dp))
            Text("WPUStudent // Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            PlainTooltipBox("Info") {
                IconButton(onClick = { /* FIXME */ }) {
                    Icon(painter = painterResource(R.drawable.outline_info_24), contentDescription = "Info")
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Enter your PwC credentials below:", fontSize = 16.sp)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth()
                    .focusProperties { next = passwordFocus }
                    .onKeyEvent { handleKeyEvent(it, Key.Enter) { passwordFocus.requestFocus() } },
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
                singleLine = true
            )
            PasswordTextField(
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(passwordFocus)
                    .focusProperties { next = loginButtonFocus }
                    .onKeyEvent { handleKeyEvent(it, Key.Enter) { login() } },
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = { login() }),
            )
            Button(
                onClick = { login() },
                modifier = Modifier.align(Alignment.End).focusRequester(loginButtonFocus)
            ) {
                Text("Login")
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
