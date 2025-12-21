package xyz.retrixe.wpustudent.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

/**
 *   Gets the singleton DataStore instance, creating it if necessary.
 *   https://developer.android.com/kotlin/multiplatform/datastore
 */
fun createDataStore(dataStoreFileName: String): DataStore<Preferences> = createDataStore(
    producePath = {
        TODO("don't store to tmpdir")
        val file = File(System.getProperty("java.io.tmpdir"), dataStoreFileName)
        file.absolutePath
    }
)
