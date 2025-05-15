package xyz.retrixe.wpustudent.screens.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import xyz.retrixe.wpustudent.R

enum class AppDestinations(val label: String, @DrawableRes val icon: Int) {
    HOME("Home", R.drawable.baseline_home_24),
    ATTENDANCE("Attendance", R.drawable.baseline_co_present_24),
}

@Composable
fun MainScreen(paddingValues: PaddingValues, accessToken: String) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(painterResource(it.icon), it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        // FIXME: Destination content.
        Box(Modifier.padding(paddingValues)) {
            Text("FIXME $accessToken")
        }
    }
}
