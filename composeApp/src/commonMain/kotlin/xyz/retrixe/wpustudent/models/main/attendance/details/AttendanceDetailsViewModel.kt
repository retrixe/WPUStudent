package xyz.retrixe.wpustudent.models.main.attendance.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.toRoute
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import xyz.retrixe.wpustudent.api.erp.endpoints.getAttendanceDetails
import xyz.retrixe.wpustudent.api.erp.entities.CourseAttendanceDetail
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.kmp.Parcelable
import xyz.retrixe.wpustudent.kmp.Parcelize
import xyz.retrixe.wpustudent.screens.Screens
import kotlin.reflect.KClass

class AttendanceDetailsViewModel(
    private val httpClient: HttpClient,
    private val savedStateHandle: SavedStateHandle,
    private val studentBasicInfo: StudentBasicInfo
) : ViewModel() {
    var data = savedStateHandle.getStateFlow<Data>("data", Data.Loading)

    init { viewModelScope.launch(Dispatchers.IO) { fetchData() } }

    suspend fun fetchData() {
        try {
            val route = savedStateHandle.toRoute<Screens.Main.Attendance.Details>()
            val summary = getAttendanceDetails(httpClient, studentBasicInfo.prn, route.courseId)
            savedStateHandle["data"] = Data.Loaded(summary)
        } catch (e: Exception) {
            Logger.w("", e, this@AttendanceDetailsViewModel::class.simpleName!!)
            savedStateHandle["data"] = Data.Error
        }
    }

    @Parcelize
    sealed interface Data : Parcelable {
        object Loading : Data

        object Error : Data

        data class Loaded(
            val details: List<CourseAttendanceDetail>,
        ) : Data
    }

    class Factory(
        private val httpClient: HttpClient,
        private val studentBasicInfo: StudentBasicInfo
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return AttendanceDetailsViewModel(
                httpClient = httpClient,
                savedStateHandle = savedStateHandle,
                studentBasicInfo = studentBasicInfo
            ) as T
        }
    }
}
