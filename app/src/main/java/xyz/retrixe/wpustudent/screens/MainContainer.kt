package xyz.retrixe.wpustudent.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import kotlinx.serialization.Serializable
import xyz.retrixe.wpustudent.R
import xyz.retrixe.wpustudent.models.SessionViewModel
import xyz.retrixe.wpustudent.screens.loading.LoadingScreen
import xyz.retrixe.wpustudent.screens.login.LoginScreen
import xyz.retrixe.wpustudent.screens.main.attendance.AttendanceScreen
import xyz.retrixe.wpustudent.screens.main.holidays.HolidaysScreen
import xyz.retrixe.wpustudent.screens.main.home.HomeScreen
import xyz.retrixe.wpustudent.screens.main.settings.SettingsScreen
import xyz.retrixe.wpustudent.state.LocalSnackbarHostState

// FIXME: Multi-window and desktop windowing support

object Screens {
    @Serializable object Loading
    @Serializable object Login
    @Serializable object Main {
        @Serializable object Home
        @Serializable object Attendance
        // FIXME: Exam screen
        @Serializable object Holidays
        @Serializable object Settings

        enum class Destinations(val label: String, @get:DrawableRes val icon: Int, val route: Any) {
            HOME("Home", R.drawable.baseline_home_24, Home),
            ATTENDANCE("Attendance", R.drawable.baseline_co_present_24, Attendance),
            HOLIDAYS("Holidays", R.drawable.baseline_calendar_month_24, Holidays),
            SETTINGS("Settings", R.drawable.baseline_settings_24, Settings),
        }
    }
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
    val httpClient by sessionViewModel.httpClient.collectAsState()
    val studentBasicInfo by sessionViewModel.studentBasicInfo.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState,
        ) {
            // https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation
            val isNavigationBarVisible =
                currentDestination?.route?.startsWith(Screens.Main::class.qualifiedName!!) == true
            NavigationSuiteScaffold(
                layoutType =
                    if (isNavigationBarVisible)
                        NavigationSuiteScaffoldDefaults
                            .calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())
                    else NavigationSuiteType.None,
                navigationSuiteItems = {
                    val mainGraph = navController.graph[Screens.Main] as NavGraph
                    Screens.Main.Destinations.entries.forEach {
                        item(
                            icon = { Icon(painterResource(it.icon), it.label) },
                            label = { Text(it.label) },
                            selected = mainGraph[it.route] == currentDestination,
                            onClick = {
                                navController.navigate(it.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(mainGraph.startDestinationId) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            ) {
                val startDestination =
                    if (loading) Screens.Loading
                    else if (accessToken == null) Screens.Login
                    else Screens.Main
                NavHost(navController = navController, startDestination = startDestination) {
                    composable<Screens.Loading> { LoadingScreen(innerPadding) }
                    composable<Screens.Login> { LoginScreen(innerPadding, sessionViewModel) }
                    navigation<Screens.Main>(startDestination = Screens.Main.Home) {
                        composable<Screens.Main.Home> {
                            HomeScreen(innerPadding, httpClient, studentBasicInfo!!)
                        }
                        composable<Screens.Main.Attendance> {
                            AttendanceScreen(innerPadding, httpClient, studentBasicInfo!!)
                        }
                        composable<Screens.Main.Holidays> {
                            HolidaysScreen(innerPadding, httpClient, studentBasicInfo!!)
                        }
                        composable<Screens.Main.Settings> {
                            SettingsScreen(innerPadding, sessionViewModel)
                        }
                    }
                }
            }
        }
    }
}
