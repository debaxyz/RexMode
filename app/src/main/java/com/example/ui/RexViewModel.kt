package com.example.ui

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ControlModuleEntity
import com.example.data.local.LicenseKeyEntity
import com.example.data.local.SystemLogEntity
import com.example.data.repository.RexRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RexViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = RexRepository(db.appDao())

    val hwid: String = generateDeviceHwid()

    var licenseKeyInput = MutableStateFlow("")
        private set

    var isKeyVisible = MutableStateFlow(false)
        private set

    var validationMessage = MutableStateFlow("• Please enter your License Key.")
        private set

    var isValidationSuccess = MutableStateFlow(false)
        private set

    var isLoading = MutableStateFlow(false)
        private set

    var selectedLanguage = MutableStateFlow("EN")
        private set

    var serverUrlInput = MutableStateFlow(repository.currentServerUrl)
        private set

    var isFastApiConnected = MutableStateFlow(false)
        private set

    var fastApiLatencyMs = MutableStateFlow(0L)
        private set

    var showServerConfigDialog = MutableStateFlow(false)
        private set

    var showPythonCodeDialog = MutableStateFlow(false)
        private set

    val activeLicense: StateFlow<LicenseKeyEntity?> = repository.activeLicense
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val systemLogs: StateFlow<List<SystemLogEntity>> = repository.systemLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val controlModules: StateFlow<List<ControlModuleEntity>> = repository.controlModules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDefaultModules()
            checkFastApiHealth()
        }
    }

    private fun generateDeviceHwid(): String {
        val model = Build.MODEL.replace(" ", "").uppercase()
        val finger = Build.FINGERPRINT.hashCode().toString(16).uppercase()
        val raw = "F500DD0337A9F4DC"
        return if (raw.length >= 16) raw else (model + finger + "F500DD0337A9F4DC").take(16)
    }

    fun onKeyInputChange(newKey: String) {
        licenseKeyInput.value = newKey
        if (newKey.isBlank()) {
            validationMessage.value = "• Please enter your License Key."
            isValidationSuccess.value = false
        } else {
            validationMessage.value = "• Press OPEN CONTROL DECK to authenticate."
            isValidationSuccess.value = false
        }
    }

    fun toggleKeyVisibility() {
        isKeyVisible.value = !isKeyVisible.value
    }

    fun setLanguage(lang: String) {
        selectedLanguage.value = lang
    }

    fun setServerConfigDialogVisible(visible: Boolean) {
        showServerConfigDialog.value = visible
    }

    fun setPythonCodeDialogVisible(visible: Boolean) {
        showPythonCodeDialog.value = visible
    }

    fun updateServerUrl(newUrl: String) {
        serverUrlInput.value = newUrl
        repository.updateServerUrl(newUrl)
        checkFastApiHealth()
    }

    fun checkFastApiHealth() {
        viewModelScope.launch {
            val (connected, latency) = repository.pingFastApi()
            isFastApiConnected.value = connected
            fastApiLatencyMs.value = latency
        }
    }

    fun verifyLicense() {
        val key = licenseKeyInput.value.ifBlank { "REX-XXXX-XXXX" }
        viewModelScope.launch {
            isLoading.value = true
            validationMessage.value = "• Connecting to FastAPI node..."
            isValidationSuccess.value = false

            val result = repository.verifyLicenseKey(key, hwid)
            isLoading.value = false

            result.onSuccess { response ->
                isValidationSuccess.value = true
                validationMessage.value = "• Access Granted: ${response.message}"
            }.onFailure { error ->
                isValidationSuccess.value = false
                validationMessage.value = "• ${error.message ?: "Authentication failed."}"
            }
        }
    }

    fun getLicenseKey() {
        viewModelScope.launch {
            isLoading.value = true
            validationMessage.value = "• Generating trial license via FastAPI..."
            
            val result = repository.generateTrialLicenseKey(hwid)
            isLoading.value = false

            result.onSuccess { newKey ->
                licenseKeyInput.value = newKey
                validationMessage.value = "• Trial Key Generated: $newKey"
                isValidationSuccess.value = true
            }.onFailure { error ->
                validationMessage.value = "• ${error.localizedMessage}"
            }
        }
    }

    fun toggleModule(code: String, currentEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleModule(code, currentEnabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            licenseKeyInput.value = ""
            validationMessage.value = "• Please enter your License Key."
            isValidationSuccess.value = false
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }
}
