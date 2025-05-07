package xyz.retrixe.wpustudent.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

// FIXME:
//  https://developer.android.com/guide/navigation?hl=en
//  https://developer.android.com/guide/navigation/navcontroller
// TODO: Multi-window and desktop windowing support
// TODO: Predictive back support
// TODO: Material You support

enum class Screens {
    Login,
    Main;
}

@Composable
fun MainContainer() {
    var screen by remember { mutableStateOf(Screens.Login) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (screen) {
            Screens.Login -> LoginScreen(innerPadding)
            Screens.Main -> TODO()
        }
    }
}
