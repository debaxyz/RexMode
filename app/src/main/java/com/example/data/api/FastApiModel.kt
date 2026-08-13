package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LicenseVerifyRequest(
    @Json(name = "license_key") val licenseKey: String,
    @Json(name = "hwid") val hwid: String
)

@JsonClass(generateAdapter = true)
data class LicenseVerifyResponse(
    @Json(name = "valid") val valid: Boolean,
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "expires_in_days") val expiresInDays: Int? = 30,
    @Json(name = "user_id") val userId: String? = "REX-USER-882",
    @Json(name = "token") val token: String? = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9...",
    @Json(name = "ecdsa_signature") val ecdsaSignature: String? = "30450221008f..."
)

@JsonClass(generateAdapter = true)
data class LicenseGenerateRequest(
    @Json(name = "hwid") val hwid: String
)

@JsonClass(generateAdapter = true)
data class LicenseGenerateResponse(
    @Json(name = "license_key") val licenseKey: String,
    @Json(name = "expires_in_days") val expiresInDays: Int,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class HealthResponse(
    @Json(name = "status") val status: String,
    @Json(name = "version") val version: String,
    @Json(name = "uptime_seconds") val uptimeSeconds: Long
)
