package xyz.retrixe.wpustudent.models

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.store.SETTINGS_ATTENDANCE_THRESHOLD_OVERRIDE
import xyz.retrixe.wpustudent.store.settingsDataStore

class SettingsViewModel(
    private val settingsDataStore: DataStore<Preferences>,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val attendanceThresholdOverride = savedStateHandle
        .getStateFlow<Int?>("attendance_threshold_override", null)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle["attendance_threshold_override"] = settingsDataStore.data
                .map { it[SETTINGS_ATTENDANCE_THRESHOLD_OVERRIDE] }
                .firstOrNull()
        }
    }

    suspend fun setAttendanceThresholdOverride(value: Int) {
        savedStateHandle["attendance_threshold_override"] = value
        settingsDataStore.edit { it[SETTINGS_ATTENDANCE_THRESHOLD_OVERRIDE] = value }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY]!!
                val savedStateHandle = createSavedStateHandle()
                SettingsViewModel(
                    application.applicationContext.settingsDataStore,
                    savedStateHandle
                )
            }
        }
    }
}
