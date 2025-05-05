package xyz.retrixe.wpustudent.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.retrixe.wpustudent.ui.components.PasswordTextField
import xyz.retrixe.wpustudent.utils.handleKeyEvent

/*
Hash seems optional

curl 'https://mymitwpu.integratededucation.pwc.in/sso/api/account/oauth2/token?hash=zOMgcPkkFAmrQQhxscW8' \
  -H 'content-type: application/json' \
  -H 'x-applicationname: oneportal' \
  -H 'x-appsecret;' \
  -H 'x-requestfrom: web' \
  --data-raw $'{"UserId":"ibrahim.ansari@mitwpu.edu.in","Password":"CENSORED","Captcha":"-","LoginUsing":"OTP","AppRef":"studentportal"}'
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
        Text("WPUStudent", fontSize = 48.sp)
        Text("Login", fontSize = 24.sp)
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
}
