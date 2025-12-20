package xyz.retrixe.wpustudent.kmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getAndroidContext(): Any? = LocalContext.current
