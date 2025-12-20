package xyz.retrixe.wpustudent.screens

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import wpustudent.composeapp.generated.resources.Res
import wpustudent.composeapp.generated.resources.baseline_calendar_month_24
import wpustudent.composeapp.generated.resources.baseline_co_present_24
import wpustudent.composeapp.generated.resources.baseline_home_24
import wpustudent.composeapp.generated.resources.baseline_settings_24
import xyz.retrixe.wpustudent.kmp.Keep

@Keep object Screens {
    @Keep @Serializable object Loading
    @Keep @Serializable object Login
    @Keep @Serializable object Main {
        @Keep @Serializable object Home
        @Keep @Serializable object Attendance {
            @Keep @Serializable data class Details(val courseId: String)
        }
        @Keep @Serializable object Exams
        @Keep
        @Serializable object Events
        @Keep @Serializable object Settings

        enum class Destinations(val label: String, val icon: DrawableResource, val route: Any) {
            HOME("Home", Res.drawable.baseline_home_24, Home),
            ATTENDANCE("Attendance", Res.drawable.baseline_co_present_24, Attendance),
            // TODO: EXAMS("Exams", Res.drawable.baseline_book_24, Exams),
            EVENTS("Events", Res.drawable.baseline_calendar_month_24, Events),
            SETTINGS("Settings", Res.drawable.baseline_settings_24, Settings),
        }
    }
}
