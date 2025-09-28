package xyz.retrixe.wpustudent.models.main.events

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import xyz.retrixe.wpustudent.api.erp.endpoints.getEvents
import xyz.retrixe.wpustudent.api.erp.entities.Event
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo

class EventsViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val studentBasicInfo: StudentBasicInfo
) : ViewModel() {
    var data = savedStateHandle.getStateFlow<Data>("data", Data.Loading)

    init { viewModelScope.launch(Dispatchers.IO) { fetchData() } }

    suspend fun fetchData() {
        try {
            val data = getEvents(studentBasicInfo.term)
            savedStateHandle["data"] = Data.Loaded(data)
        } catch (e: Exception) {
            Log.w(this@EventsViewModel::class.simpleName, e)
            savedStateHandle["data"] = Data.Error
        }
    }

    @Parcelize
    sealed interface Data : Parcelable {
        object Loading : Data

        object Error : Data

        data class Loaded(val events: List<Event>) : Data
    }

    class Factory(
        private val studentBasicInfo: StudentBasicInfo
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return EventsViewModel(
                savedStateHandle = savedStateHandle,
                studentBasicInfo = studentBasicInfo
            ) as T
        }
    }
}
