package xyz.retrixe.wpustudent.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(paddingValues: PaddingValues, accessToken: String) {
    // FIXME: Destination content.
    Box(Modifier.padding(paddingValues)) {
        Text("FIXME $accessToken")
    }
}
