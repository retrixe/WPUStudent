package xyz.retrixe.wpustudent.models.main.attendance

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import xyz.retrixe.wpustudent.api.erp.endpoints.getAttendanceSummary
import xyz.retrixe.wpustudent.api.erp.entities.CourseAttendanceSummary

class AttendanceViewModel(
    private val httpClient: HttpClient,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var data = savedStateHandle.getStateFlow<Data>("data", Data.Loading)

    init { viewModelScope.launch(Dispatchers.IO) { fetchData() } }

    suspend fun fetchData() {
        try {
            val summary = getAttendanceSummary(httpClient)
            savedStateHandle["data"] = Data.Loaded(summary)
        } catch (e: Exception) {
            Log.w(this@AttendanceViewModel::class.simpleName, e)
            savedStateHandle["data"] = Data.Error
        }
    }

    @Parcelize
    sealed interface Data : Parcelable {
        object Loading : Data

        object Error : Data

        data class Loaded(
            val summary: List<CourseAttendanceSummary>,
        ) : Data
    }

    class Factory(
        private val httpClient: HttpClient,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return AttendanceViewModel(
                httpClient = httpClient,
                savedStateHandle = savedStateHandle
            ) as T
        }
    }
}
