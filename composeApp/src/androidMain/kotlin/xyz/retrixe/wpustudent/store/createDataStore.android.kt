package xyz.retrixe.wpustudent.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 *   Gets the singleton DataStore instance, creating it if necessary.
 *   https://developer.android.com/kotlin/multiplatform/datastore
 */
fun createDataStore(context: Context, dataStoreFileName: String): DataStore<Preferences> =
    createDataStore { context.filesDir.resolve("datastore") // Backwards compat
        .resolve(dataStoreFileName).absolutePath }
