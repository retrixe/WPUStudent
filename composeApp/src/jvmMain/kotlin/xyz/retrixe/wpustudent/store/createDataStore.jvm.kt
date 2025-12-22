package xyz.retrixe.wpustudent.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import net.harawata.appdirs.AppDirsFactory
import java.io.File

val configDir: String = AppDirsFactory
    .getInstance()
    .getUserConfigDir("WPUStudent", null, null)

/**
 *   Gets the singleton DataStore instance, creating it if necessary.
 *   https://developer.android.com/kotlin/multiplatform/datastore
 */
fun createDataStore(dataStoreFileName: String): DataStore<Preferences> = createDataStore(
    producePath = {
        val file = File(configDir, dataStoreFileName)
        file.absolutePath
    }
)
