package com.example.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FastApiService {
    @POST("/api/v1/auth/verify-license")
    suspend fun verifyLicense(
        @Body request: LicenseVerifyRequest
    ): Response<LicenseVerifyResponse>

    @POST("/api/v1/license/generate")
    suspend fun generateLicenseKey(
        @Body request: LicenseGenerateRequest
    ): Response<LicenseGenerateResponse>

    @GET("/api/v1/health")
    suspend fun healthCheck(): Response<HealthResponse>
}
