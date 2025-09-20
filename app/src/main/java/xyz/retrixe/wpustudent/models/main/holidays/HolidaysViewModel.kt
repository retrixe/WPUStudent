package xyz.retrixe.wpustudent.models.main.holidays

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
import xyz.retrixe.wpustudent.api.erp.endpoints.getHolidays
import xyz.retrixe.wpustudent.api.erp.entities.Holiday

class HolidaysViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var data = savedStateHandle.getStateFlow<Data>("data", Data.Loading)

    init { viewModelScope.launch(Dispatchers.IO) { fetchData() } }

    fun fetchData() {
        try {
            val data = getHolidays()
            savedStateHandle["data"] = Data.Loaded(data)
        } catch (e: Exception) {
            Log.w(this@HolidaysViewModel::class.simpleName, e)
            savedStateHandle["data"] = Data.Error
        }
    }

    @Parcelize
    sealed interface Data : Parcelable {
        object Loading : Data

        object Error : Data

        data class Loaded(val holidays: List<Holiday>) : Data
    }

    class Factory() : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return HolidaysViewModel(
                savedStateHandle = savedStateHandle
            ) as T
        }
    }
}
