package xyz.retrixe.wpustudent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import xyz.retrixe.wpustudent.store.createDataStore
import xyz.retrixe.wpustudent.store.sessionDataStore
import xyz.retrixe.wpustudent.store.SESSION_DATA_STORE_FILE_NAME
import xyz.retrixe.wpustudent.store.settingsDataStore
import xyz.retrixe.wpustudent.store.SETTINGS_DATA_STORE_FILE_NAME
import xyz.retrixe.wpustudent.store.isSessionDataStoreInitialized
import xyz.retrixe.wpustudent.store.isSettingsDataStoreInitialized

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (!isSessionDataStoreInitialized())
            sessionDataStore = createDataStore(applicationContext, SESSION_DATA_STORE_FILE_NAME)
        if (!isSettingsDataStoreInitialized())
            settingsDataStore = createDataStore(applicationContext, SETTINGS_DATA_STORE_FILE_NAME)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
