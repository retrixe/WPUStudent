package xyz.retrixe.wpustudent.screens.main.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.retrixe.wpustudent.utils.handleKeyEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceThresholdDialog(
    initialValue: Int?,
    onSubmit: (Int?) -> Unit,
    onDismissRequest: () -> Unit
) {
    var newValue by remember { mutableStateOf(initialValue) }

    BasicAlertDialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("Attendance Threshold Override",
                    fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth()
                        .onKeyEvent { handleKeyEvent(it, Key.Enter) { onSubmit(newValue) } },
                    value = newValue?.toString() ?: "",
                    onValueChange = {
                        newValue = if (it.isEmpty()) null else {
                            val tempValue = it.toIntOrNull()
                            if (tempValue == null || tempValue > 100) return@OutlinedTextField
                            tempValue
                        }
                    },
                    placeholder = { Text("Default: No override") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onNext = { onSubmit(newValue) }),
                    singleLine = true
                )
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton({ onDismissRequest() }) {
                        Text("Close")
                    }
                    Button({ onSubmit(newValue) }) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
