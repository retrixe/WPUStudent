package xyz.retrixe.wpustudent.kmp

import androidx.compose.ui.platform.UriHandler
import kotlinx.datetime.LocalDate

actual fun openCalendar(androidContext: Any?, uriHandler: UriHandler, date: LocalDate) {
    // FIXME-KMP: Support Windows
    // FIXME-KMP: Support macOS (probably just works with the iOS technique)
    // FIXME-KMP: Support GNOME
    // FIXME-KMP: Support KDE? Not my problem, actually.
    throw UnsupportedOperationException("Opening the calendar is unsupported on this system.")
}
