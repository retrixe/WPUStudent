package xyz.retrixe.wpustudent.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import xyz.retrixe.wpustudent.R

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    modifier: Modifier,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(value = value, onValueChange = onValueChange,
        enabled = enabled,
        label = label,
        supportingText = supportingText,
        isError = isError,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
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
        visualTransformation =
            if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation()
    )
}
