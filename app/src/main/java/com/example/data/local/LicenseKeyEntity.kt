package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "license_keys")
data class LicenseKeyEntity(
    @PrimaryKey val key: String,
    val hwid: String,
    val status: String, // "ACTIVE", "EXPIRED", "INVALID"
    val activatedAt: Long,
    val expiresAt: Long,
    val serverUrl: String,
    val token: String
)
