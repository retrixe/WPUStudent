package xyz.retrixe.wpustudent.models.main.exams

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
import xyz.retrixe.wpustudent.api.pwc.endpoints.getExams
import xyz.retrixe.wpustudent.api.pwc.entities.ExamHallTicket
import xyz.retrixe.wpustudent.api.pwc.entities.StudentBasicInfo

class ExamsViewModel(
    private val httpClient: HttpClient,
    private val studentBasicInfo: StudentBasicInfo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var data = savedStateHandle.getStateFlow<Data>("data", Data.Loading)

    init { viewModelScope.launch(Dispatchers.IO) { fetchData() } }

    suspend fun fetchData() {
        try {
            val data = getExams(httpClient, studentBasicInfo.studentId, studentBasicInfo.termCode)
            savedStateHandle["data"] = Data.Loaded(data)
        } catch (e: Exception) {
            Log.w(this@ExamsViewModel::class.simpleName, e)
            savedStateHandle["data"] = Data.Error
        }
    }

    @Parcelize
    sealed interface Data : Parcelable {
        object Loading : Data

        object Error : Data

        data class Loaded(val data: ExamHallTicket) : Data
    }

    class Factory(
        private val httpClient: HttpClient,
        private val studentBasicInfo: StudentBasicInfo
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return ExamsViewModel(
                httpClient = httpClient,
                studentBasicInfo = studentBasicInfo,
                savedStateHandle = savedStateHandle
            ) as T
        }
    }
}
