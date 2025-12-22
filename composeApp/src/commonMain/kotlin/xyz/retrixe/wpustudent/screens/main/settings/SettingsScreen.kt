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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.api.erp.entities.THRESHOLD_PERCENTAGE
import xyz.retrixe.wpustudent.models.SessionViewModel
import xyz.retrixe.wpustudent.models.SettingsViewModel
import xyz.retrixe.wpustudent.ui.components.AboutDialog
import xyz.retrixe.wpustudent.ui.components.PlainTooltipBox
import org.jetbrains.compose.resources.painterResource
import wpustudent.composeapp.generated.resources.Res
import wpustudent.composeapp.generated.resources.baseline_logout_24
import wpustudent.composeapp.generated.resources.outline_info_24
import xyz.retrixe.wpustudent.BuildKonfig
import xyz.retrixe.wpustudent.kmp.collectAsStateWithLifecycleMultiplatform

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    sessionViewModel: SessionViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    var attendanceThresholdDialog by remember { mutableStateOf(false) }
    var aboutDialog by remember { mutableStateOf(false) }
    var loggingOut by remember { mutableStateOf(false) }

    val attendanceThreshold by settingsViewModel.attendanceThreshold.collectAsStateWithLifecycleMultiplatform()

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
                    Icon(painter = painterResource(Res.drawable.outline_info_24), contentDescription = "Info")
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
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(24.dp))
        val topRounded = (CardDefaults.shape as RoundedCornerShape).copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        )
        val bottomRounded = (CardDefaults.shape as RoundedCornerShape).copy(
            topStart = CornerSize(0.dp),
            topEnd = CornerSize(0.dp),
        )
        Card(
            { uriHandler.openUri("https://github.com/retrixe/WPUStudent") },
            Modifier.width(512.dp).fillMaxWidth(),
            shape = topRounded
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Source Code on GitHub", fontSize = 20.sp)
                Text("https://github.com/retrixe/WPUStudent",
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        HorizontalDivider(Modifier.width(512.dp).fillMaxWidth())
        Card(
            { uriHandler.openUri("https://github.com/retrixe/WPUStudent/blob/${BuildKonfig.VERSION_NAME}/LICENSE") },
            Modifier.width(512.dp).fillMaxWidth(),
            shape = bottomRounded
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("License", fontSize = 20.sp)
                Text("Mozilla Public License 2.0",
                    fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                painter = painterResource(Res.drawable.baseline_logout_24),
                contentDescription = "Logout"
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text("Logout")
        }
    }
}
