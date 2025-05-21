package xyz.retrixe.wpustudent.screens.main.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.R
import xyz.retrixe.wpustudent.models.SessionViewModel

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    sessionViewModel: SessionViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    fun logout() = coroutineScope.launch { sessionViewModel.logout() }

    Column(
        Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Settings",
            fontSize = 36.sp,
            modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(24.dp))
        Button(
            { logout() },
            Modifier.width(512.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_logout_24),
                contentDescription = "Logout"
            )
            Spacer(Modifier.width(8.dp))
            Text("Logout")
        }
    }
}
