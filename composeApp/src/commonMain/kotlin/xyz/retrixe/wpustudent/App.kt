package xyz.retrixe.wpustudent

import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

import xyz.retrixe.wpustudent.screens.MainContainer
import xyz.retrixe.wpustudent.ui.theme.WPUStudentTheme

@Composable
@Preview
fun App() {
    WPUStudentTheme {
        MainContainer()
    }
}
