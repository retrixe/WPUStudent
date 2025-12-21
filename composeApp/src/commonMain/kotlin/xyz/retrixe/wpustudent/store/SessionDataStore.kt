package xyz.retrixe.wpustudent.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey

const val SESSION_DATA_STORE_FILE_NAME = "session.preferences_pb"

// Epic, Kotlin/Native has no synchronization primitives or volatile.
// Let's just ignore thread safety...
// .....What could go wrong?

lateinit var sessionDataStore: DataStore<Preferences>

fun isSessionDataStoreInitialized() = ::sessionDataStore.isInitialized

val SESSION_ACCESS_TOKEN = stringPreferencesKey("access_token")
val SESSION_ACCOUNT_DETAILS = stringPreferencesKey("account_details")
