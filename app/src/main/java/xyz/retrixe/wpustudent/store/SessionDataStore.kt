package xyz.retrixe.wpustudent.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "session",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

val SESSION_ACCESS_TOKEN = stringPreferencesKey("access_token")
val SESSION_ACCOUNT_DETAILS = stringPreferencesKey("account_details")
