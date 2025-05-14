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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.R
import xyz.retrixe.wpustudent.models.SessionViewModel
import xyz.retrixe.wpustudent.state.LocalSnackbarHostState
import xyz.retrixe.wpustudent.ui.components.PasswordTextField
import xyz.retrixe.wpustudent.ui.components.PlainTooltipBox
import xyz.retrixe.wpustudent.utils.handleKeyEvent

@Composable
fun LoginScreen(paddingValues: PaddingValues, sessionViewModel: SessionViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val (passwordFocus, loginButtonFocus) = remember { FocusRequester.createRefs() }

    fun login() = coroutineScope.launch(Dispatchers.IO) {
        try {
            sessionViewModel.login(username, password)
        } catch (e: Exception) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    e.localizedMessage ?: "Unknown error",
                    withDismissAction = true
                )
            }
        }
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
