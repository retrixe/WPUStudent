package xyz.retrixe.wpustudent.kmp

import kotlinx.datetime.LocalDate

expect fun openCalendar(androidContext: Any?, date: LocalDate)

expect fun openUrl(androidContext: Any?, url: String)
