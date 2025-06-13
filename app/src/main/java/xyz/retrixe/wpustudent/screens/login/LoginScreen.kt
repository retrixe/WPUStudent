package xyz.retrixe.wpustudent.screens.login

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import xyz.retrixe.wpustudent.R
import xyz.retrixe.wpustudent.api.json
import xyz.retrixe.wpustudent.models.SessionViewModel
import xyz.retrixe.wpustudent.state.LocalSnackbarHostState
import xyz.retrixe.wpustudent.ui.components.AboutDialog
import xyz.retrixe.wpustudent.ui.components.PasswordTextField
import xyz.retrixe.wpustudent.ui.components.PlainTooltipBox
import xyz.retrixe.wpustudent.utils.handleKeyEvent

@Serializable private data class LoginError(@SerialName("Message") val message: String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(paddingValues: PaddingValues, sessionViewModel: SessionViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current
    val (passwordFocus, rememberPasswordFocus, loginButtonFocus) =
        remember { FocusRequester.createRefs() }

    var aboutDialog by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var rememberPassword by remember { mutableStateOf(true) }

    fun login() = coroutineScope.launch(Dispatchers.IO) {
        loading = true
        try {
            sessionViewModel.login(email, password, rememberPassword)
            loading = false
        } catch (e: ClientRequestException) {
            loading = false
            val data: LoginError = json.decodeFromString(e.response.bodyAsText())
            snackbarHostState.showSnackbar(
                data.message ?: e.localizedMessage,
                withDismissAction = true
            )
        } catch (e: Exception) {
            loading = false
            snackbarHostState.showSnackbar(
                e.localizedMessage ?: "Unknown error",
                withDismissAction = true
            )
        }
    }

    if (aboutDialog) {
        AboutDialog { aboutDialog = false }
    }

    Column(
        Modifier.padding(paddingValues).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(48.dp))
            Text("WPUStudent // Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            PlainTooltipBox("Info") {
                IconButton(onClick = { aboutDialog = true }) {
                    Icon(painter = painterResource(R.drawable.outline_info_24), contentDescription = "Info")
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Column(
            Modifier.padding(horizontal = 16.dp).width(512.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Enter your MIT-WPU PwC login details:")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth()
                    .focusProperties { next = passwordFocus }
                    .onKeyEvent { handleKeyEvent(it, Key.Enter) { passwordFocus.requestFocus() } },
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                enabled = !loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
                singleLine = true
            )
            PasswordTextField(
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(passwordFocus)
                    .focusProperties { next = rememberPasswordFocus }
                    .onKeyEvent { handleKeyEvent(it, Key.Enter) { login() } },
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                enabled = !loading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = { login() }),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Remember Password")
                Checkbox(
                    modifier = Modifier
                        .focusRequester(rememberPasswordFocus)
                        .focusProperties { next = loginButtonFocus },
                    enabled = !loading,
                    checked = rememberPassword,
                    onCheckedChange = { rememberPassword = it }
                )
            }
            Button(
                onClick = { login() },
                modifier = Modifier.align(Alignment.End).focusRequester(loginButtonFocus),
                enabled = !loading && !email.isBlank() && !password.isBlank(),
            ) {
                Text("Login")
            }
        }
        Spacer(Modifier.weight(1f))
    }
}
