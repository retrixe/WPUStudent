package xyz.retrixe.wpustudent.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.TextFieldLabelScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import xyz.retrixe.wpustudent.R

@Composable
fun PasswordTextField(
    state: TextFieldState,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions,
    onKeyboardAction: KeyboardActionHandler,
    label: @Composable (TextFieldLabelScope.() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    modifier: Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedSecureTextField(
        state = state,
        enabled = enabled,
        label = label,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        modifier = modifier,
        trailingIcon = {
            val image =
                if (passwordVisible) R.drawable.baseline_visibility_24
                else R.drawable.baseline_visibility_off_24
            val description = if (passwordVisible) "Hide password" else "Show password"
            PlainTooltipBox(description) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(painter = painterResource(image), contentDescription = description)
                }
            }
        },
        textObfuscationMode =
            if (passwordVisible) TextObfuscationMode.Visible
            else TextObfuscationMode.RevealLastTyped
    )
}
