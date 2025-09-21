package xyz.retrixe.wpustudent.screens.main.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.R
import xyz.retrixe.wpustudent.api.erp.entities.THRESHOLD_PERCENTAGE
import xyz.retrixe.wpustudent.models.SessionViewModel
import xyz.retrixe.wpustudent.models.SettingsViewModel
import xyz.retrixe.wpustudent.ui.components.AboutDialog
import xyz.retrixe.wpustudent.ui.components.PlainTooltipBox

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    sessionViewModel: SessionViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    var attendanceThresholdDialog by remember { mutableStateOf(false) }
    var aboutDialog by remember { mutableStateOf(false) }
    var loggingOut by remember { mutableStateOf(false) }

    val attendanceThreshold by settingsViewModel.attendanceThreshold.collectAsState()

    fun logout() = coroutineScope.launch {
        loggingOut = true
        sessionViewModel.logout()
    }

    if (aboutDialog) {
        AboutDialog { aboutDialog = false }
    }

    if (attendanceThresholdDialog) {
        AttendanceThresholdDialog(attendanceThreshold, { coroutineScope.launch {
            settingsViewModel.setAttendanceThreshold(it)
            attendanceThresholdDialog = false
        } }, {
            attendanceThresholdDialog = false
        })
    }

    Column(
        Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Settings", fontSize = 36.sp)
            PlainTooltipBox("Info") {
                IconButton(onClick = { aboutDialog = true }) {
                    Icon(painter = painterResource(R.drawable.outline_info_24), contentDescription = "Info")
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Card(
            { attendanceThresholdDialog = true },
            Modifier.width(512.dp).fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Attendance Threshold", fontSize = 20.sp)
                Text(attendanceThreshold?.toString() ?: "Default: ${THRESHOLD_PERCENTAGE.toInt() + 5}%",
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            { logout() },
            Modifier.width(512.dp).fillMaxWidth(),
            enabled = !loggingOut,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_logout_24),
                contentDescription = "Logout"
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text("Logout")
        }
    }
}
