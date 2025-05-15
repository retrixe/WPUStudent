package xyz.retrixe.wpustudent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import xyz.retrixe.wpustudent.screens.MainContainer
import xyz.retrixe.wpustudent.ui.theme.WPUStudentTheme

// TODO: Multi-window and desktop windowing support
// TODO: Material You support

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WPUStudentTheme {
                MainContainer()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    WPUStudentTheme {
        MainContainer()
    }
}
