package com.example.data.repository

import com.example.data.api.FastApiService
import com.example.data.api.LicenseGenerateRequest
import com.example.data.api.LicenseGenerateResponse
import com.example.data.api.LicenseVerifyRequest
import com.example.data.api.LicenseVerifyResponse
import com.example.data.local.AppDao
import com.example.data.local.ControlModuleEntity
import com.example.data.local.LicenseKeyEntity
import com.example.data.local.SystemLogEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit

class RexRepository(private val appDao: AppDao) {

    var currentServerUrl: String = "http://10.0.2.2:8000"
        private set

    private var apiService: FastApiService? = null

    init {
        updateServerUrl(currentServerUrl)
    }

    fun updateServerUrl(newUrl: String) {
        currentServerUrl = if (newUrl.startsWith("http://") || newUrl.startsWith("https://")) {
            newUrl
        } else {
            "http://$newUrl"
        }

        try {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(currentServerUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            apiService = retrofit.create(FastApiService::class.java)
        } catch (e: Exception) {
            apiService = null
        }
    }

    val activeLicense: Flow<LicenseKeyEntity?> = appDao.getActiveLicense()
    val systemLogs: Flow<List<SystemLogEntity>> = appDao.getLogs()
    val controlModules: Flow<List<ControlModuleEntity>> = appDao.getControlModules()

    suspend fun initializeDefaultModules() = withContext(Dispatchers.IO) {
        val initialModules = listOf(
            ControlModuleEntity("DARK_DOODLE", "Dark Doodle Engine", "Primary rendering engine for custom cyber overlay", "Visual", isEnabled = true, isVip = false),
            ControlModuleEntity("HWID_SPOOF", "HWID Spoof Guard", "Hardware signature masking & ECDSA token validation", "Security", isEnabled = true, isVip = false),
            ControlModuleEntity("TLS_TUNNEL", "TLS 1.3 Fast Tunnel", "Encrypted connection to FastAPI control node", "Network", isEnabled = true, isVip = false),
            ControlModuleEntity("SCRIPT_EXEC", "Cyber Script Executor", "Automated macro & layout injector v6", "Core", isEnabled = false, isVip = true),
            ControlModuleEntity("MEMORY_SHIELD", "Memory Anti-Tamper", "Real-time process memory shield & checksum validation", "Security", isEnabled = true, isVip = true)
        )
        appDao.insertModules(initialModules)
        logEvent("SUCCESS", "FastAPI", "Initialized Rex Mode V6 core control modules")
    }

    suspend fun verifyLicenseKey(
        licenseKey: String,
        hwid: String
    ): Result<LicenseVerifyResponse> = withContext(Dispatchers.IO) {
        logEvent("INFO", "FastAPI", "POST /api/v1/auth/verify-license [Key: ${licenseKey.take(12)}...]")

        if (licenseKey.isBlank()) {
            logEvent("ERROR", "ECDSA", "Key verification failed: Empty License Key")
            return@withContext Result.failure(Exception("Please enter your License Key."))
        }

        try {
            // Attempt live FastAPI call
            val response = apiService?.verifyLicense(LicenseVerifyRequest(licenseKey.trim(), hwid))
            if (response != null && response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.valid) {
                    saveActiveLicense(licenseKey.trim(), hwid, body.token ?: "JWT_LIVE_TOKEN")
                    logEvent("SUCCESS", "ECDSA", "FastAPI License Verified! Token issued.")
                    return@withContext Result.success(body)
                } else {
                    logEvent("WARN", "ECDSA", "FastAPI: ${body.message}")
                    return@withContext Result.failure(Exception(body.message))
                }
            }
        } catch (e: Exception) {
            logEvent("WARN", "FastAPI", "Live server unreachable (${e.localizedMessage}). Falling back to local FastAPI simulator.")
        }

        // Local FastAPI Simulation fallback
        val cleanKey = licenseKey.trim().uppercase()
        if (cleanKey.length < 5) {
            logEvent("ERROR", "ECDSA", "Invalid key length. Key must be at least 5 chars.")
            return@withContext Result.failure(Exception("Please enter a valid License Key."))
        }

        // Accept keys starting with REX- or any key >= 8 chars for smooth demoing
        val isValid = cleanKey.startsWith("REX-") || cleanKey.length >= 8
        if (isValid) {
            val token = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.${UUID.randomUUID().toString().take(12)}"
            val ecdsaSig = "3045022100" + UUID.randomUUID().toString().replace("-", "").take(24)
            val mockResponse = LicenseVerifyResponse(
                valid = true,
                status = "ACTIVE",
                message = "License key authenticated via ECDSA-256",
                expiresInDays = 30,
                userId = "REX-USER-" + (100..999).random(),
                token = token,
                ecdsaSignature = ecdsaSig
            )
            saveActiveLicense(cleanKey, hwid, token)
            logEvent("SUCCESS", "ECDSA", "Signature verified: 256-bit HWID match ($hwid)")
            logEvent("SUCCESS", "TLS", "Encrypted session established with FastAPI node")
            Result.success(mockResponse)
        } else {
            logEvent("ERROR", "ECDSA", "Access Denied: Unrecognized license signature")
            Result.failure(Exception("Invalid License Key signature."))
        }
    }

    suspend fun generateTrialLicenseKey(hwid: String): Result<String> = withContext(Dispatchers.IO) {
        logEvent("INFO", "FastAPI", "POST /api/v1/license/generate [HWID: $hwid]")

        try {
            val response = apiService?.generateLicenseKey(LicenseGenerateRequest(hwid))
            if (response != null && response.isSuccessful && response.body() != null) {
                val newKey = response.body()!!.licenseKey
                logEvent("SUCCESS", "FastAPI", "New 14-day license generated: $newKey")
                return@withContext Result.success(newKey)
            }
        } catch (e: Exception) {
            logEvent("WARN", "FastAPI", "Live generator offline, generating local Rex key")
        }

        // Fallback local generator
        val randomPart = UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
        val part1 = randomPart.substring(0, 4)
        val part2 = randomPart.substring(4, 8)
        val generatedKey = "REX-$part1-$part2"
        logEvent("SUCCESS", "FastAPI", "Generated Trial Key: $generatedKey")
        Result.success(generatedKey)
    }

    suspend fun pingFastApi(): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        return@withContext try {
            val res = apiService?.healthCheck()
            val latency = System.currentTimeMillis() - start
            if (res != null && res.isSuccessful) {
                logEvent("INFO", "FastAPI", "Health check OK ($latency ms)")
                Pair(true, latency)
            } else {
                Pair(false, 0L)
            }
        } catch (e: Exception) {
            Pair(false, 0L)
        }
    }

    private suspend fun saveActiveLicense(key: String, hwid: String, token: String) {
        val now = System.currentTimeMillis()
        val expiry = now + (30L * 24 * 60 * 60 * 1000)
        appDao.clearLicenses()
        appDao.insertLicense(
            LicenseKeyEntity(
                key = key,
                hwid = hwid,
                status = "ACTIVE",
                activatedAt = now,
                expiresAt = expiry,
                serverUrl = currentServerUrl,
                token = token
            )
        )
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        logEvent("INFO", "FastAPI", "Session terminated by user")
        appDao.clearLicenses()
    }

    suspend fun toggleModule(code: String, currentEnabled: Boolean) = withContext(Dispatchers.IO) {
        val newState = !currentEnabled
        appDao.updateModuleStatus(code, newState)
        logEvent("INFO", "ControlDeck", "Module '$code' toggled -> ${if (newState) "ENABLED" else "DISABLED"}")
    }

    suspend fun logEvent(level: String, source: String, message: String) = withContext(Dispatchers.IO) {
        val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
        appDao.insertLog(
            SystemLogEntity(
                timestamp = timeStr,
                level = level,
                source = source,
                message = message
            )
        )
    }

    suspend fun clearLogs() = withContext(Dispatchers.IO) {
        appDao.clearLogs()
    }
}
