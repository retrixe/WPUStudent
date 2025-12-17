package xyz.retrixe.wpustudent.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

actual val platformColorSchemeAvailable = false

@Composable
actual fun platformColorScheme(darkTheme: Boolean): ColorScheme =
    throw UnsupportedOperationException("No platform color scheme available on desktop")
