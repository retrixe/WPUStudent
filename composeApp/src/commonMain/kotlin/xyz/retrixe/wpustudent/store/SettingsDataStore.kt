package xyz.retrixe.wpustudent.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

const val SETTINGS_DATA_STORE_FILE_NAME = "settings.preferences_pb"

// Epic, Kotlin/Native has no synchronization primitives or volatile.
// Let's just ignore thread safety...
// .....What could go wrong?

lateinit var settingsDataStore: DataStore<Preferences>

fun isSettingsDataStoreInitialized() = ::settingsDataStore.isInitialized

val SETTINGS_ATTENDANCE_THRESHOLD = intPreferencesKey("attendance_threshold")
