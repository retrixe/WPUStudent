package xyz.retrixe.wpustudent

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import xyz.retrixe.wpustudent.store.SESSION_DATA_STORE_FILE_NAME
import xyz.retrixe.wpustudent.store.SETTINGS_DATA_STORE_FILE_NAME
import xyz.retrixe.wpustudent.store.createDataStore
import xyz.retrixe.wpustudent.store.sessionDataStore
import xyz.retrixe.wpustudent.store.settingsDataStore

@OptIn(ExperimentalForeignApi::class)
fun MainViewController() = ComposeUIViewController {
    sessionDataStore = remember { createDataStore(SESSION_DATA_STORE_FILE_NAME) }
    settingsDataStore = remember { createDataStore(SETTINGS_DATA_STORE_FILE_NAME) }

    App()
}
