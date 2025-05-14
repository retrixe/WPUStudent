package xyz.retrixe.wpustudent.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.retrixe.wpustudent.models.SessionViewModel
import xyz.retrixe.wpustudent.state.LocalSnackbarHostState

// FIXME:
//  https://developer.android.com/guide/navigation?hl=en
//  https://developer.android.com/guide/navigation/navcontroller
// TODO: Multi-window and desktop windowing support
// TODO: Predictive back support
// TODO: Material You support

enum class Screens {
    Loading,
    Login,
    Main;
}

@Composable
fun MainContainer(
    sessionViewModel: SessionViewModel = viewModel(factory = SessionViewModel.Factory)
) {
    var screen by remember { mutableStateOf(Screens.Loading) }
    val snackbarHostState = remember { SnackbarHostState() }

    val loading by sessionViewModel.loading.collectAsState()
    val accessToken by sessionViewModel.accessToken.collectAsState()
    val studentBasicInfo by sessionViewModel.studentBasicInfo.collectAsState(null)

    LaunchedEffect(loading, accessToken) {
        screen =
            if (loading) Screens.Loading
            else if (accessToken != null) Screens.Main
            else Screens.Login
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState,
        ) {
            when (screen) {
                Screens.Loading -> LoadingScreen()
                Screens.Login -> LoginScreen(innerPadding, sessionViewModel)
                Screens.Main -> MainScreen("$accessToken $studentBasicInfo")
            }
        }
    }
}
