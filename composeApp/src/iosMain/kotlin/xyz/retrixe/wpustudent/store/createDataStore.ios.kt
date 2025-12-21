package xyz.retrixe.wpustudent.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

/**
 *   Gets the singleton DataStore instance, creating it if necessary.
 *   https://developer.android.com/kotlin/multiplatform/datastore
 */
@OptIn(ExperimentalForeignApi::class)
fun createDataStore(dataStoreFileName: String): DataStore<Preferences> = createDataStore(
    producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/" + dataStoreFileName
    }
)
