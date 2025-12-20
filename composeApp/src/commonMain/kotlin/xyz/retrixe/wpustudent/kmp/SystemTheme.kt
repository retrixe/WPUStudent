package xyz.retrixe.wpustudent.kmp

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

expect val platformColorSchemeAvailable: Boolean

@Composable
expect fun platformColorScheme(darkTheme: Boolean): ColorScheme
