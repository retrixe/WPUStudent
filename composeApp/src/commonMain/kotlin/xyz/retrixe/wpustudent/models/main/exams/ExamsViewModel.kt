package xyz.retrixe.wpustudent.models.main.exams

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.api.erp.endpoints.getExams
import xyz.retrixe.wpustudent.api.erp.entities.ExamHallTicket
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.kmp.Parcelable
import xyz.retrixe.wpustudent.kmp.Parcelize
import kotlin.reflect.KClass

class ExamsViewModel(
    private val httpClient: HttpClient,
    private val studentBasicInfo: StudentBasicInfo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var data = savedStateHandle.getStateFlow<Data>("data", Data.Loading)

    init { viewModelScope.launch(Dispatchers.IO) { fetchData() } }

    suspend fun fetchData() {
        try {
            val data = getExams(httpClient, studentBasicInfo.prn, studentBasicInfo.prn)
            savedStateHandle["data"] = Data.Loaded(data)
        } catch (e: Exception) {
            Logger.w("", e, this@ExamsViewModel::class.simpleName!!)
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
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return ExamsViewModel(
                httpClient = httpClient,
                studentBasicInfo = studentBasicInfo,
                savedStateHandle = savedStateHandle
            ) as T
        }
    }
}
