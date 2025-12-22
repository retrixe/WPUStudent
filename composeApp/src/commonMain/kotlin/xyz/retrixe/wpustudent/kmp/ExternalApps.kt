package xyz.retrixe.wpustudent.kmp

import androidx.compose.ui.platform.UriHandler
import kotlinx.datetime.LocalDate

expect fun openCalendar(androidContext: Any?, uriHandler: UriHandler, date: LocalDate)
