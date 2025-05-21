package xyz.retrixe.wpustudent.models

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import xyz.retrixe.wpustudent.api.StudentBasicInfo
import xyz.retrixe.wpustudent.api.createHttpClient
import xyz.retrixe.wpustudent.api.getAccessToken
import xyz.retrixe.wpustudent.api.getOAuthCode
import xyz.retrixe.wpustudent.api.retrieveStudentBasicInfo
import xyz.retrixe.wpustudent.store.SESSION_ACCESS_TOKEN
import xyz.retrixe.wpustudent.store.decryptFromString
import xyz.retrixe.wpustudent.store.encryptToString
import xyz.retrixe.wpustudent.store.sessionDataStore

class SessionViewModel(
    private val sessionDataStore: DataStore<Preferences>,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var loading = savedStateHandle
        .getStateFlow("loading", true)
    val accessToken = savedStateHandle
        .getStateFlow<String?>("access_token", null)
    val studentBasicInfo = savedStateHandle
        .getStateFlow<String?>("student_basic_info", null)
        .map { it?.let { Json.decodeFromString<StudentBasicInfo>(it) } }
    val httpClient = accessToken
        .map { createHttpClient(it) }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!loading.value) return@launch

            // Retrieve token from DataStore
            val accessToken = sessionDataStore.data
                .map { it[SESSION_ACCESS_TOKEN] }
                .map { it?.let { decryptFromString(SESSION_ACCESS_TOKEN.name, it) } }
                .firstOrNull()

            // If no access token, we're done here.
            if (accessToken == null) {
                savedStateHandle["loading"] = false
                return@launch
            }

            try {
                val studentBasicInfo = retrieveStudentBasicInfo(httpClient.first(), accessToken)
                savedStateHandle["access_token"] = accessToken
                savedStateHandle["student_basic_info"] = Json.encodeToString(studentBasicInfo)
            } catch (e: Exception) {
                Log.w("SessionViewModel", e)
            }
            savedStateHandle["loading"] = false
        }
    }

    suspend fun login(username: String, password: String) {
        val httpClient = createHttpClient(null)
        val code = getOAuthCode(httpClient, username, password)
        val accessToken = getAccessToken(httpClient, code)
        val studentBasicInfo = retrieveStudentBasicInfo(httpClient, accessToken)
        val encryptedAccessToken = encryptToString(SESSION_ACCESS_TOKEN.name, accessToken)
        sessionDataStore.edit { it[SESSION_ACCESS_TOKEN] = encryptedAccessToken }
        savedStateHandle["access_token"] = accessToken
        savedStateHandle["student_basic_info"] = Json.encodeToString(studentBasicInfo)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = extras[APPLICATION_KEY]!!
                val savedStateHandle = extras.createSavedStateHandle()
                return SessionViewModel(
                    application.applicationContext.sessionDataStore,
                    savedStateHandle
                ) as T
            }
        }
    }
}
