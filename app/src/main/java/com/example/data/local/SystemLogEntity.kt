package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_logs")
data class SystemLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: String,
    val level: String, // "INFO", "SUCCESS", "WARN", "ERROR"
    val source: String, // "FastAPI", "ECDSA", "HWID", "TLS"
    val message: String
)
