package xyz.retrixe.wpustudent.models

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import xyz.retrixe.wpustudent.api.erp.createHttpClient
import xyz.retrixe.wpustudent.api.erp.endpoints.login
import xyz.retrixe.wpustudent.api.erp.endpoints.retrieveStudentBasicInfo
import xyz.retrixe.wpustudent.api.erp.entities.StudentBasicInfo
import xyz.retrixe.wpustudent.store.SESSION_ACCESS_TOKEN
import xyz.retrixe.wpustudent.store.SESSION_ACCOUNT_DETAILS
import xyz.retrixe.wpustudent.store.decryptFromString
import xyz.retrixe.wpustudent.store.encryptToString
import xyz.retrixe.wpustudent.store.sessionDataStore
import kotlin.reflect.KClass

class SessionViewModel(
    private val sessionDataStore: DataStore<Preferences>,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _vanillaHttpClient = createHttpClient(null)

    var loading = savedStateHandle
        .getStateFlow("loading", true)
    val accessToken = savedStateHandle
        .getStateFlow<String?>("access_token", null)
    val studentBasicInfo = savedStateHandle
        .getStateFlow<String?>("student_basic_info", null)
        .map { it?.let { Json.decodeFromString<StudentBasicInfo>(it) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val httpClient = accessToken
        .map { createHttpClient(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _vanillaHttpClient)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (!loading.value) return@launch

            // Retrieve token and account details from DataStore
            val accessToken = sessionDataStore.data
                .map { it[SESSION_ACCESS_TOKEN] }
                .map { it?.let { decryptFromString(SESSION_ACCESS_TOKEN.name, it) } }
                .firstOrNull()
            val accountDetails = sessionDataStore.data
                .map { it[SESSION_ACCOUNT_DETAILS] }
                .map { it?.let { decryptFromString(SESSION_ACCOUNT_DETAILS.name, it)?.split(":") } }
                .firstOrNull()

            // If no access token, we're done here.
            if (accessToken == null) {
                savedStateHandle["loading"] = false
                return@launch
            }

            try {
                try {
                    val studentBasicInfo = retrieveStudentBasicInfo(httpClient.first())
                    savedStateHandle["access_token"] = accessToken
                    savedStateHandle["student_basic_info"] = Json.encodeToString(studentBasicInfo)
                } catch (e: Exception) {
                    if (accountDetails == null) throw e
                    else login(accountDetails[0], accountDetails[1], false)
                }
            } catch (e: Exception) {
                Logger.w("", e, this@SessionViewModel::class.simpleName!!)
            }
            savedStateHandle["loading"] = false
        }
    }

    suspend fun login(username: String, password: String, saveDetails: Boolean) {
        val httpClient = _vanillaHttpClient
        val authToken = login(httpClient, username, password)
        val studentBasicInfo = retrieveStudentBasicInfo(createHttpClient(authToken))
        val encryptedAccessToken = encryptToString(SESSION_ACCESS_TOKEN.name, authToken)
        val accountDetails = "$username:$password"
        val encryptedAccountDetails = encryptToString(SESSION_ACCOUNT_DETAILS.name, accountDetails)
        sessionDataStore.edit {
            it[SESSION_ACCESS_TOKEN] = encryptedAccessToken
            if (saveDetails) it[SESSION_ACCOUNT_DETAILS] = encryptedAccountDetails
        }
        savedStateHandle["access_token"] = authToken
        savedStateHandle["student_basic_info"] = Json.encodeToString(studentBasicInfo)
    }

    suspend fun logout() {
        try {
            xyz.retrixe.wpustudent.api.erp.endpoints.logout(httpClient.first())
        } catch (e: Exception) {
            Logger.w("", e, this@SessionViewModel::class.simpleName!!)
        }
        sessionDataStore.edit {
            it.remove(SESSION_ACCESS_TOKEN)
            it.remove(SESSION_ACCOUNT_DETAILS)
        }
        savedStateHandle["access_token"] = null
        savedStateHandle["student_basic_info"] = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
                //val application = extras[APPLICATION_KEY]!!
                val savedStateHandle = extras.createSavedStateHandle()
                return SessionViewModel(
                    sessionDataStore, //application.applicationContext.sessionDataStore,
                    savedStateHandle
                ) as T
            }
        }
    }
}
