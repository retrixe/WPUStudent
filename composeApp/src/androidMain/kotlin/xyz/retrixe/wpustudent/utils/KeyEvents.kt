package xyz.retrixe.wpustudent.utils

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type

fun handleKeyEvent(event: KeyEvent, key: Key, handler: () -> Unit) =
    // KeyDown is bust on Linux
    if (event.key == key && event.type == KeyEventType.KeyUp) {
        handler()
        true
    } else false
