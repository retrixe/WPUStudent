package xyz.retrixe.wpustudent.models.main.home

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
import xyz.retrixe.wpustudent.api.erp.entities.StudentInfo
import xyz.retrixe.wpustudent.api.pwc.endpoints.fetchAsset
import java.io.Serializable

class HomeViewModel(
    private val httpClient: HttpClient,
    private val studentInfo: StudentInfo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var data = savedStateHandle.getStateFlow<Data>("data", Data.Loading)

    init { viewModelScope.launch(Dispatchers.IO) { fetchData() } }

    suspend fun fetchData() {
        try {
            val asset = fetchAsset(httpClient,
                "iemsfilecontainer",
                studentInfo.profilePictureInfo.filePath,
                "profile-picture.png")
            savedStateHandle["data"] = Data.Loaded(asset)
        } catch (e: Exception) {
            Log.w(this@HomeViewModel::class.simpleName, e)
            savedStateHandle["data"] = Data.Error
        }
    }

    sealed interface Data : Serializable {
        object Loading : Data {
            @Suppress("unused") private fun readResolve(): Any = Loading
        }

        object Error : Data {
            @Suppress("unused") private fun readResolve(): Any = Error
        }

        data class Loaded(val profilePicture: ByteArray) : Data {
            override fun equals(other: Any?) =
                this === other &&
                        javaClass == other.javaClass &&
                        profilePicture.contentEquals(other.profilePicture)

            override fun hashCode() = profilePicture.contentHashCode()
        }
    }

    class Factory(
        private val httpClient: HttpClient,
        private val studentInfo: StudentInfo
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return HomeViewModel(
                httpClient = httpClient,
                studentInfo = studentInfo,
                savedStateHandle = savedStateHandle
            ) as T
        }
    }
}
