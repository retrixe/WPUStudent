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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import xyz.retrixe.wpustudent.models.SessionViewModel
import xyz.retrixe.wpustudent.screens.loading.LoadingScreen
import xyz.retrixe.wpustudent.screens.login.LoginScreen
import xyz.retrixe.wpustudent.screens.main.MainScreen
import xyz.retrixe.wpustudent.state.LocalSnackbarHostState

// FIXME: Predictive back support
// TODO: Multi-window and desktop windowing support
// TODO: Material You support

object Screens {
    @Serializable object Loading
    @Serializable object Login
    @Serializable object Main
}

@Composable
fun MainContainer(
    sessionViewModel: SessionViewModel = viewModel(factory = SessionViewModel.Factory)
) {
    // https://developer.android.com/develop/ui/compose/navigation
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val loading by sessionViewModel.loading.collectAsState()
    val accessToken by sessionViewModel.accessToken.collectAsState()
    val studentBasicInfo by sessionViewModel.studentBasicInfo.collectAsState(null)

    LaunchedEffect(loading, accessToken) {
        // Default is Loading, we never return
        if (loading) return@LaunchedEffect
        else if (accessToken == null) {
            navController.navigate(Screens.Login) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            navController.graph.setStartDestination(Screens.Login)
        } else {
            navController.navigate(Screens.Main) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            navController.graph.setStartDestination(Screens.Main)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState,
        ) {
            NavHost(navController = navController, startDestination = Screens.Loading) {
                composable<Screens.Loading> { LoadingScreen(innerPadding) }
                composable<Screens.Login> { LoginScreen(innerPadding, sessionViewModel) }
                composable<Screens.Main> { MainScreen(innerPadding, "$accessToken $studentBasicInfo") }
            }
        }
    }
}
